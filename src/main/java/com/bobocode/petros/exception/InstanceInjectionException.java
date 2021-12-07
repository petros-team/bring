package com.bobocode.petros.exception;

public class InstanceInjectionException extends RuntimeException {
    private String message;

    public InstanceInjectionException(String message) {
        this.message = message;
    }

    public InstanceInjectionException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return String.format("Can't create object from dependency definition because of %s", message);
    }
}
