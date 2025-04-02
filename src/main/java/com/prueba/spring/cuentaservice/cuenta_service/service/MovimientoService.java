package com.prueba.spring.cuentaservice.cuenta_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.prueba.spring.cuentaservice.cuenta_service.entity.Cuenta;
import com.prueba.spring.cuentaservice.cuenta_service.entity.Movimiento;
import com.prueba.spring.cuentaservice.cuenta_service.exception.ResourceNotFoundException;
import com.prueba.spring.cuentaservice.cuenta_service.kafka.KafkaProducerService;
import com.prueba.spring.cuentaservice.cuenta_service.kafka.KafkaReplyListener;
import com.prueba.spring.cuentaservice.cuenta_service.repository.CuentaRepository;
import com.prueba.spring.cuentaservice.cuenta_service.repository.MovimientoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
                throw new RuntimeException("Timeout al validar cliente");
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        boolean clienteValido = KafkaReplyListener.getRespuestas().remove(correlationId);
        if (!clienteValido) {
            throw new RuntimeException("Cliente no válido");
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
        if ("DEPOSITO".equalsIgnoreCase(movimiento.getTipoMovimiento())) {
            return saldoActual + movimiento.getValor();
        } else if ("RETIRO".equalsIgnoreCase(movimiento.getTipoMovimiento())) {
            if (saldoActual < movimiento.getValor()) {
                throw new ResourceNotFoundException("Saldo insuficiente");
            }
            return saldoActual - movimiento.getValor();
        } else {
            throw new RuntimeException("Tipo de movimiento inválido");
        }
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
}
