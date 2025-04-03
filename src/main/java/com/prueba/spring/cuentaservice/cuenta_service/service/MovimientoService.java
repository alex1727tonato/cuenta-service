package com.prueba.spring.cuentaservice.cuenta_service.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.prueba.spring.cuentaservice.cuenta_service.entity.Cuenta;
import com.prueba.spring.cuentaservice.cuenta_service.entity.Movimiento;
import com.prueba.spring.cuentaservice.cuenta_service.exception.BadRequestException;
import com.prueba.spring.cuentaservice.cuenta_service.exception.ResourceNotFoundException;
import com.prueba.spring.cuentaservice.cuenta_service.kafka.KafkaProducerService;
import com.prueba.spring.cuentaservice.cuenta_service.kafka.KafkaReplyListener;
import com.prueba.spring.cuentaservice.cuenta_service.repository.CuentaRepository;
import com.prueba.spring.cuentaservice.cuenta_service.repository.MovimientoRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MovimientoService {
    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;
    private final KafkaProducerService kafkaProducerService;

    public List<Movimiento> listarMovimientos() {
        return movimientoRepository.findAll();
    }

    public Movimiento crearMovimiento(Long cuentaId, Movimiento movimiento) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        String correlationId = UUID.randomUUID().toString();
        kafkaProducerService.solicitarValidacionCliente(cuenta.getClienteId().toString(), correlationId);

        // Espera activa con timeout máximo de 5s
        long start = System.currentTimeMillis();
        while (!KafkaReplyListener.getRespuestas().containsKey(correlationId)) {
            if (System.currentTimeMillis() - start > 5000) {
                throw new BadRequestException("Timeout al validar cliente");
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        boolean clienteValido = KafkaReplyListener.getRespuestas().remove(correlationId);
        if (!clienteValido) {
            throw new BadRequestException("Cliente no valido");
        }

        movimiento.setCuenta(cuenta);
        movimiento.setFecha(LocalDateTime.now());

        double nuevoSaldo = calcularNuevoSaldo(cuenta.getSaldoInicial(), movimiento);
        movimiento.setSaldo(nuevoSaldo);

        cuenta.setSaldoInicial(nuevoSaldo);
        cuentaRepository.save(cuenta);

        return movimientoRepository.save(movimiento);
    }

    private double calcularNuevoSaldo(Double saldoActual, Movimiento movimiento) {
        if (movimiento.getValor() < 0) {
            if (saldoActual < Math.abs(movimiento.getValor())) {
                throw new BadRequestException("Saldo insuficiente");
            }
        }
        return saldoActual + movimiento.getValor();
    }

    // private double calcularNuevoSaldoPorTipo(Double saldoActual, Movimiento
    // movimiento) {
    // if ("DEPOSITO".equalsIgnoreCase(movimiento.getTipoMovimiento())) {
    // return saldoActual + movimiento.getValor();
    // } else if ("RETIRO".equalsIgnoreCase(movimiento.getTipoMovimiento())) {
    // if (saldoActual < movimiento.getValor()) {
    // throw new BadRequestException("Saldo insuficiente");
    // }
    // return saldoActual - movimiento.getValor();
    // } else {
    // throw new BadRequestException("Tipo de movimiento no válido");
    // }
    // }

    public Movimiento actualizarMovimiento(Long id, Movimiento movimientoActualizado) {
        Movimiento movimientoExistente = movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado"));

        movimientoExistente.setFecha(movimientoActualizado.getFecha());
        movimientoExistente.setTipoMovimiento(movimientoActualizado.getTipoMovimiento());
        movimientoExistente.setValor(movimientoActualizado.getValor());
        movimientoExistente.setSaldo(movimientoActualizado.getSaldo());

        return movimientoRepository.save(movimientoExistente);
    }

    public List<Movimiento> listarPorCuenta(Long cuentaId) {
        return movimientoRepository.findByCuentaId(cuentaId);
    }

    public Optional<Movimiento> obtenerMovimientoPorId(Long id) {
        return movimientoRepository.findById(id);
    }

    public void eliminarMovimiento(Long id) {
        movimientoRepository.deleteById(id);
    }

    public List<Map<String, Object>> generarReporte(LocalDate startDate, LocalDate endDate, Long clientId) {
        List<Cuenta> cuentas = (clientId != null)
                ? cuentaRepository.findByClienteId(clientId)
                : cuentaRepository.findAll();

        List<Map<String, Object>> reporte = new ArrayList<>();

        for (Cuenta cuenta : cuentas) {
            List<Movimiento> movimientos = movimientoRepository
                    .findByCuentaIdAndFechaBetween(cuenta.getId(), startDate.atStartOfDay(),
                            endDate.plusDays(1).atStartOfDay());

            for (Movimiento mov : movimientos) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("Fecha", mov.getFecha().toLocalDate());
                fila.put("Cliente", "Cliente ID " + cuenta.getClienteId());
                fila.put("Numero Cuenta", cuenta.getNumeroCuenta());
                fila.put("Tipo", cuenta.getTipoCuenta());
                fila.put("Saldo Inicial", mov.getSaldo() - mov.getValor());
                fila.put("Estado", cuenta.getEstado());
                fila.put("Movimiento", mov.getValor());
                fila.put("Saldo Disponible", mov.getSaldo());
                reporte.add(fila);
            }
        }

        return reporte;
    }

}
