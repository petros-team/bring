package com.bobocode.petros.injector;

import com.bobocode.petros.scaner.AnnotationDependencyConfigurationScanner;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;


class AnnotationDependencyInjectorTest {
    private AnnotationDependencyInjector sut;
    private AnnotationDependencyConfigurationScanner configurationScanner;
    private static final String TEST_TARGET_PATH = "target/test-classes/";
    private static final String TEST_PACKAGE = "com.bobocode.petros.testpackage";

    @SneakyThrows
    @BeforeEach
    void setup() {
        configurationScanner = new AnnotationDependencyConfigurationScanner();
        var configScannerClass = configurationScanner.getClass();
        var targetClassesPathField = configScannerClass.getDeclaredField("targetClassesPath");
        targetClassesPathField.setAccessible(true);
        targetClassesPathField.set(configurationScanner, TEST_TARGET_PATH);
        sut = new AnnotationDependencyInjector(TEST_PACKAGE);
        final Class<? extends AnnotationDependencyInjector> sutClass = sut.getClass();
        final Field configScannerField = sutClass.getDeclaredField("configurationScanner");
        configScannerField.setAccessible(true);
        configScannerField.set(sut, configurationScanner);
    }

    @Test
    void injectedDependencyDefinitionObjectMap() {
        // TODO update test
        // final Map<DependencyDefinition, Object> dependencyDefinitionObjectMap = sut.injectedDependencyDefinitionObjectMap();
        // assertEquals(dependencyDefinitionObjectMap.size(), 5);
    }
}