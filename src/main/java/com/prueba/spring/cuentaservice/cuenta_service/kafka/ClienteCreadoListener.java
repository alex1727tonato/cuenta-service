package com.prueba.spring.cuentaservice.cuenta_service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.prueba.spring.cuentaservice.cuenta_service.entity.Cuenta;
import com.prueba.spring.cuentaservice.cuenta_service.repository.CuentaRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClienteCreadoListener {
    private final CuentaRepository cuentaRepository;

    @KafkaListener(topics = "cliente-creado", groupId = "cuentas-group")
    public void escucharClienteCreado(String clienteId) {
        log.info("Recibido cliente creado: {}", clienteId);
        Cuenta cuenta = new Cuenta();
        cuenta.setClienteId(Long.parseLong(clienteId));
        cuenta.setNumeroCuenta("AHO-" + clienteId);
        cuenta.setTipoCuenta("Ahorro");
        cuenta.setSaldoInicial(0.0);
        cuenta.setEstado(true);
        cuentaRepository.save(cuenta);
        log.info("Cuenta de ahorro creada para cliente {}", clienteId);
    }
}
