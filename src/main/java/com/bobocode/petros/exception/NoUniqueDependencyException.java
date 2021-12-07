package com.bobocode.petros.exception;

public class NoUniqueDependencyException extends RuntimeException {
    private final String className;

    public NoUniqueDependencyException(String className) {
        this.className = className;
    }

    @Override
    public String getMessage() {
        return String.format("There is more than one Dependency with such type %s found", className);
    }
}
