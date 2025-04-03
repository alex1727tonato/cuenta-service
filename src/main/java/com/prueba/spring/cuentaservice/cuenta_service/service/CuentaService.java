package com.prueba.spring.cuentaservice.cuenta_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.prueba.spring.cuentaservice.cuenta_service.entity.Cuenta;
import com.prueba.spring.cuentaservice.cuenta_service.exception.ResourceNotFoundException;
import com.prueba.spring.cuentaservice.cuenta_service.repository.CuentaRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CuentaService {

    private final CuentaRepository cuentaRepository;

    public Cuenta crearCuenta(Cuenta cuenta) {
        return cuentaRepository.save(cuenta);
    }

    public Cuenta actualizarCuenta(Long id, Cuenta cuentaActualizada) {
        Cuenta cuentaExistente = cuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada"));

        cuentaExistente.setNumeroCuenta(cuentaActualizada.getNumeroCuenta());
        cuentaExistente.setTipoCuenta(cuentaActualizada.getTipoCuenta());
        cuentaExistente.setSaldoInicial(cuentaActualizada.getSaldoInicial());
        cuentaExistente.setEstado(cuentaActualizada.getEstado());
        cuentaExistente.setClienteId(cuentaActualizada.getClienteId());

        return cuentaRepository.save(cuentaExistente);
    }

    public List<Cuenta> listarCuentas() {
        return cuentaRepository.findAll();
    }

    public Optional<Cuenta> obtenerCuentaPorId(Long id) {
        return cuentaRepository.findById(id);
    }

    public void eliminarCuenta(Long id) {
        cuentaRepository.deleteById(id);
    }
}
