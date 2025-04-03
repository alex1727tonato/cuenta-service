package com.prueba.spring.cuentaservice.cuenta_service.controller;

import lombok.RequiredArgsConstructor;

import org.apache.kafka.shaded.io.opentelemetry.proto.resource.v1.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.prueba.spring.cuentaservice.cuenta_service.entity.Movimiento;
import com.prueba.spring.cuentaservice.cuenta_service.exception.ResourceNotFoundException;
import com.prueba.spring.cuentaservice.cuenta_service.service.MovimientoService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

    @PutMapping("/{id}")
    public ResponseEntity<Movimiento> actualizar(@PathVariable Long id, @RequestBody Movimiento movimientoActualizado) {
        return ResponseEntity.ok(movimientoService.actualizarMovimiento(id, movimientoActualizado));
    }

    @GetMapping("/cuenta/{cuentaId}")
    public ResponseEntity<List<Movimiento>> listarPorCuenta(@PathVariable Long cuentaId) {
        return ResponseEntity.ok(movimientoService.listarPorCuenta(cuentaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movimiento> obtener(@PathVariable Long id) {
        return movimientoService.obtenerMovimientoPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        movimientoService.eliminarMovimiento(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reporte")
    public ResponseEntity<List<Map<String, Object>>> generarReporte(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "client", required = false) Long clientId) {
        return ResponseEntity.ok(movimientoService.generarReporte(startDate, endDate, clientId));
    }
}
