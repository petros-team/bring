package com.bobocode.petros.exception;

public class NotPublicConstructorModifiers extends RuntimeException{


    public NotPublicConstructorModifiers(String message) {
        super(message);
    }
}
