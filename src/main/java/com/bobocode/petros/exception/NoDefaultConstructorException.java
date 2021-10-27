package com.bobocode.petros.exception;

public class NoDefaultConstructorException extends RuntimeException {
    private final String classQualifiedName;

    public NoDefaultConstructorException(String classQualifiedName) {
        this.classQualifiedName = classQualifiedName;
    }

    @Override
    public String getMessage() {
        return String.format("No default constructor found for class %s. Please, declare default constructor", classQualifiedName);
    }
}
