package com.prueba.spring.cuentaservice.cuenta_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.prueba.spring.cuentaservice.cuenta_service.entity.Cuenta;
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
