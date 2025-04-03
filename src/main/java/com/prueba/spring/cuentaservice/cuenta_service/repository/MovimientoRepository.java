package com.prueba.spring.cuentaservice.cuenta_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.prueba.spring.cuentaservice.cuenta_service.entity.Movimiento;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    List<Movimiento> findByCuentaId(Long cuentaId);

    List<Movimiento> findByCuentaIdAndFechaBetween(Long cuentaId, LocalDateTime start, LocalDateTime end);
}