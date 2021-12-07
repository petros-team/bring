package com.bobocode.petros.exception;

public class DependencyClassNotFoundException extends RuntimeException {

    private String message;

    public DependencyClassNotFoundException(String message) {
        this.message = message;
    }

    public DependencyClassNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return String.format("Class %s not found", message);
    }
}
