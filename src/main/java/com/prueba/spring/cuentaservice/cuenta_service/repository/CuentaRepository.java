package com.prueba.spring.cuentaservice.cuenta_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.prueba.spring.cuentaservice.cuenta_service.entity.Cuenta;
import java.util.Optional;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);
}
