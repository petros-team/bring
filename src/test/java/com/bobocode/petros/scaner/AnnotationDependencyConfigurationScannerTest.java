package com.bobocode.petros.scaner;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnnotationDependencyConfigurationScannerTest {
    private static final String SOME_PACKAGE = "some.package";

    private AnnotationDependencyConfigurationScanner sut;

    @BeforeEach
    void setup() {
        sut = new AnnotationDependencyConfigurationScanner(SOME_PACKAGE);
    }

    @Test
    void scan() {
        Assertions.assertNull(sut.scan(SOME_PACKAGE));
    }
}