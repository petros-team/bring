package com.bobocode.petros;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HelloTest {
    private Hello sut;

    @BeforeEach
    void setup() {
        sut = new Hello();
    }

    @Test
    void ifHelloThenYoSupBro() {
        var expected = "Yo! Sup bro?!";
        var actual = sut.hello();
        assertEquals(expected, actual);
    }
}