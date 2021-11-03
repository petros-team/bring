package com.bobocode.petros.container;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

class ApplicationAnnotationContainerTest {
    private ApplicationAnnotationContainer sut;
    private Map<String, Object> dependencies = new HashMap<>();

    @BeforeEach
    void setup(){
        sut = new ApplicationAnnotationContainer("some.package");
        Class<? extends ApplicationAnnotationContainer> testContainer = sut.getClass();
        dependencies.put("name",Object.class);
        try {
            Field field = testContainer.getDeclaredField("dependencyMap");
            field.setAccessible(true);
            field.set(sut, dependencies);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getDependencyByNameAndClass() {
        Assertions.assertNotNull(sut.getDependency("name", Object.class));
    }

    @Test
    void getDependencyByClass() {
        Assertions.assertNotNull(sut.getDependency(Object.class));
    }
}