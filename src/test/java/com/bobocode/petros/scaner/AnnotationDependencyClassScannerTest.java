package com.bobocode.petros.scaner;

import com.bobocode.petros.container.DependencyDefinition;
import com.bobocode.petros.exception.DefaultConstructorNotFoundException;
import com.bobocode.petros.exception.MultipleInjectConstructorsException;
import com.bobocode.petros.exception.NoSuchPackageFoundException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnnotationDependencyClassScannerTest {

    private static final String TEST_TARGET_PATH = "target/test-classes/";
    private static final String TEST_PACKAGE = "com.bobocode.petros.testpackage";
    private static String PATH_TO_CLASS = "com.bobocode.petros.testpackage.scan.dependency.SuccessfullyBuildDefinitionDependencyCase";
    private static String DEFINITION_NAME = "successfullyBuildDefinitionDependencyCase";
    private static String QUALIFIED_NAME = "com.bobocode.petros.testpackage.scan.dependency.SuccessfullyBuildDefinitionDependencyCase";

    private AnnotationDependencyClassScanner sut;

    @BeforeEach
    @SneakyThrows
    @SuppressWarnings("all")
    void setup() {
        sut = new AnnotationDependencyClassScanner();
        var sutClass = sut.getClass();
        var targetClassesPathField = sutClass.getDeclaredField("targetClassesPath");
        targetClassesPathField.setAccessible(true);
        targetClassesPathField.set(sut, TEST_TARGET_PATH);
    }

    @Test
    public void ifScanSuccessfulThenValidDependencyDefinitions() {
        var classMap = sut.scan(TEST_PACKAGE);
        var dependencyDefinitions = classMap.get(PATH_TO_CLASS);
        var dependencyDefinition = dependencyDefinitions.get(0);
        var internCollectionDefinition = dependencyDefinition.getDependencyDefinitions();
        var configClassDependency = dependencyDefinition.isConfigClassDependency();
        var definitionName = dependencyDefinition.getName();
        var qualifiedName = dependencyDefinition.getQualifiedName();
        var parametersQualifiedName = internCollectionDefinition.stream()
                .map(DependencyDefinition::getQualifiedName)
                .collect(Collectors.toList());

        assertAll(
                () -> assertEquals(1, dependencyDefinitions.size()),
                () -> assertEquals(2, internCollectionDefinition.size()),
                () -> assertTrue(internCollectionDefinition.stream().anyMatch(a -> a.getName().equals("testName"))),
                () -> assertTrue(internCollectionDefinition.stream().anyMatch(a -> a.getName().equals("listStringTest"))),
                () -> assertEquals("java.lang.String", parametersQualifiedName.get(0)),
                () -> assertEquals("java.util.ArrayList",parametersQualifiedName.get(1)),
                () -> assertTrue(classMap.containsKey(PATH_TO_CLASS)),
                () -> assertFalse(configClassDependency),
                () -> assertEquals(DEFINITION_NAME, definitionName),
                () -> assertEquals(QUALIFIED_NAME, qualifiedName),
                () -> assertDoesNotThrow(() -> new DefaultConstructorNotFoundException(QUALIFIED_NAME)),
                () -> assertDoesNotThrow(() -> new MultipleInjectConstructorsException(QUALIFIED_NAME)),
                () -> assertDoesNotThrow(() -> new ClassNotFoundException(QUALIFIED_NAME)),
                () -> assertDoesNotThrow(() -> new NoSuchPackageFoundException(PATH_TO_CLASS))
        );
    }

    @Test
    void ifInvalidPackageNameThenThrowNoSuchPackageFoundException() {
        var invalidPackageName = "invalid.package";
        var noSuchPackageFoundException = assertThrows(NoSuchPackageFoundException.class, () -> sut.scan(invalidPackageName));
        var expectedMessage = String.format("No package with name = %s found", invalidPackageName);
        assertEquals(expectedMessage, noSuchPackageFoundException.getMessage());
    }
}