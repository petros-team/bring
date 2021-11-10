package com.bobocode.petros.container;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

class ApplicationAnnotationContainerTest {
    private ApplicationAnnotationContainer sut;
    private Map<DependencyDefinition, Object> dependencies = new HashMap<>();
    private DependencyDefinition dependencyDefinition;

    @BeforeEach
    @SneakyThrows
    void setup() {
        Constructor<ApplicationAnnotationContainer> sutConstructor = ApplicationAnnotationContainer.class.getDeclaredConstructor();
        sutConstructor.setAccessible(true);
        sut = sutConstructor.newInstance();
        dependencyDefinition = new DependencyDefinition();
        dependencyDefinition.setName("name");
        Class<? extends ApplicationAnnotationContainer> testContainer = sut.getClass();
        dependencies.put(dependencyDefinition, new Object());
        Field field = testContainer.getDeclaredField("dependencyMap");
        field.setAccessible(true);
        field.set(sut, dependencies);
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