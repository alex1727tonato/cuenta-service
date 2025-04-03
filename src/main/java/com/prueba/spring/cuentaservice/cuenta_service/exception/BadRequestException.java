package com.prueba.spring.cuentaservice.cuenta_service.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
