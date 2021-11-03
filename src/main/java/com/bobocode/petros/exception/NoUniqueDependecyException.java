package com.bobocode.petros.exception;

public class NoUniqueDependecyException extends RuntimeException {
    private final String className;

    public NoUniqueDependecyException(String message) {
        this.className = message;
    }

    @Override
    public String getMessage() {
        return String.format("There is more than one Dependency with such type %s found", className);
    }
}
