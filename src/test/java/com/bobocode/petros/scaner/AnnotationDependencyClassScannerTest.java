package com.bobocode.petros.scaner;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AnnotationDependencyClassScannerTest {
    private static final String SOME_PACKAGE = "some.package";

    private AnnotationDependencyClassScanner sut;

    @BeforeEach
    void setup() {
        sut = new AnnotationDependencyClassScanner();
    }

    @Test
    void scan() {
        Assertions.assertNull(sut.scan(SOME_PACKAGE));
    }
}