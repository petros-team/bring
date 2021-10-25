package com.bobocode.petros.container;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ApplicationAnnotationContainerTest {
    private ApplicationAnnotationContainer sut;

    @BeforeEach
    void setup(){
        sut = new ApplicationAnnotationContainer("some.package");
    }

    @Test
    void getDependencyByNameAndClass() {
        Assertions.assertNull(sut.getDependency("name", Object.class));
    }

    @Test
    void getDependencyByClass() {
        Assertions.assertNull(sut.getDependency(Object.class));
    }
}