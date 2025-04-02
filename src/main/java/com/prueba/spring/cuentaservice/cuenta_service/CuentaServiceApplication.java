package com.prueba.spring.cuentaservice.cuenta_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class CuentaServiceApplication {

	@Value("${spring.kafka.bootstrap-servers:NOT_SET}")
	private String kafkaServers;

	public static void main(String[] args) {
		SpringApplication.run(CuentaServiceApplication.class, args);
	}

	@PostConstruct
	public void logKafkaServers() {
		System.out.println("✅ Kafka bootstrap-servers configurado como: " + kafkaServers);
	}

}
