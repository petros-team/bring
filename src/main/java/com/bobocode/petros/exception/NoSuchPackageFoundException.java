package com.bobocode.petros.exception;

public class NoSuchPackageFoundException extends RuntimeException {
    private final String packageName;

    public NoSuchPackageFoundException(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public String getMessage() {
        return String.format("No package with name = %s found", packageName);
    }
}
