package com.prueba.spring.cuentaservice.cuenta_service.kafka;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class KafkaReplyListener {
    @Getter
    private static final ConcurrentHashMap<String, Boolean> respuestas = new ConcurrentHashMap<>();

    @KafkaListener(topics = "respuesta-validacion-cliente", groupId = "cuentas-validation-group")
    public void escucharRespuestaValidacion(ConsumerRecord<String, String> record) {
        String correlationId = new String(record.headers().lastHeader("correlationId").value());
        boolean existe = Boolean.parseBoolean(record.value());
        log.info("Respuesta validación cliente: correlationId={} existe={}", correlationId, existe);
        respuestas.put(correlationId, existe);
    }
}
