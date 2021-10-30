package com.bobocode.petros.injector;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class AnnotationDependencyInjectorTest {
    private AnnotationDependencyInjector sut;

    @BeforeEach
    void setup(){
        sut = new AnnotationDependencyInjector("some.package");
    }

    @Test
    void injectedDependencyDefinitionObjectMap() {
        Assertions.assertNull(sut.injectedDependencyDefinitionObjectMap());
    }
}