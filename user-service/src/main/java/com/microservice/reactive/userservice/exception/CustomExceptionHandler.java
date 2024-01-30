package com.microservice.reactive.userservice.exception;


public class CustomExceptionHandler extends RuntimeException {
    public CustomExceptionHandler(String message) {
        super(message);
    }
    public CustomExceptionHandler(String message, Exception e) {
        super(message);
    }
}
