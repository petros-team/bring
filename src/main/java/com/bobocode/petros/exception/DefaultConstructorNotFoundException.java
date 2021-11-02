package com.bobocode.petros.exception;

public class DefaultConstructorNotFoundException extends RuntimeException {
    private final String className;

    public DefaultConstructorNotFoundException(String className) {
        this.className = className;
    }

    @Override
    public String getMessage() {
        return String.format("Class %s doesn't have default constructor", className);
    }
}