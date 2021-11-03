package com.bobocode.petros.exception;

public class DependencyClassNotFoundException extends RuntimeException {
    public DependencyClassNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
