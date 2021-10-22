package com.bobocode.petros.exception;

/**
 * Throw when in class doesn't have default constructor (without parameter).
 * or class have constructor with more one parameters
 */

public class DefaultNoArgsConstructor extends RuntimeException {


    public DefaultNoArgsConstructor(String message) {
        super(message);
    }
}
