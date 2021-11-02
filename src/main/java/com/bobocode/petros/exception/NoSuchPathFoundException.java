package com.bobocode.petros.exception;

public class NoSuchPathFoundException extends RuntimeException {
    private String message;

    public NoSuchPathFoundException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return String.format("No path = %s found", message);
    }
}
