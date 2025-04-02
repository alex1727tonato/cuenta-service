package com.prueba.spring.cuentaservice.cuenta_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.prueba.spring.cuentaservice.cuenta_service.entity.Movimiento;
import com.prueba.spring.cuentaservice.cuenta_service.service.MovimientoService;

import java.util.List;

@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
public class MovimientoController {
    private final MovimientoService movimientoService;

    @GetMapping
    public ResponseEntity<List<Movimiento>> listar() {
        return ResponseEntity.ok(movimientoService.listarMovimientos());
    }

    @PostMapping("/{cuentaId}")
    public ResponseEntity<Movimiento> crear(@PathVariable Long cuentaId, @RequestBody Movimiento movimiento) {
        return ResponseEntity.ok(movimientoService.crearMovimiento(cuentaId, movimiento));
    }

    @GetMapping("/cuenta/{cuentaId}")
    public ResponseEntity<List<Movimiento>> listarPorCuenta(@PathVariable Long cuentaId) {
        return ResponseEntity.ok(movimientoService.listarPorCuenta(cuentaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movimiento> obtener(@PathVariable Long id) {
        return movimientoService.obtenerMovimientoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        movimientoService.eliminarMovimiento(id);
        return ResponseEntity.noContent().build();
    }
}
