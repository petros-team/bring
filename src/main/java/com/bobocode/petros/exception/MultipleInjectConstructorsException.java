package com.bobocode.petros.exception;

public class MultipleInjectConstructorsException extends RuntimeException {

    private final String className;

    public MultipleInjectConstructorsException(String className) {
        this.className = className;
    }

    @Override
    public String getMessage() {
        return String.format("Class %s has more that one constructor with @Inject annotation", className);
    }
}
