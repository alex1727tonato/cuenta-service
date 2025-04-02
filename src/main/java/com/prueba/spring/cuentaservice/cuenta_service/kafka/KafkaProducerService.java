package com.prueba.spring.cuentaservice.cuenta_service.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void solicitarValidacionCliente(String clienteId, String correlationId) {
        ProducerRecord<String, String> record = new ProducerRecord<>("validar-cliente", clienteId);
        record.headers().add(new RecordHeader("correlationId", correlationId.getBytes()));
        kafkaTemplate.send(record);
    }
}
