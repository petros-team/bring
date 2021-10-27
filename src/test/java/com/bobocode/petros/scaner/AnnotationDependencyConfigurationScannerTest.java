package com.bobocode.petros.scaner;

import com.bobocode.petros.exception.NoSuchPackageFoundException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class AnnotationDependencyConfigurationScannerTest {
    private static final String TEST_TARGET_PATH = "target/test-classes/";
    private static final String TEST_PACKAGE = "com.bobocode.petros.testpackage";

    private AnnotationDependencyConfigurationScanner sut;

    @BeforeEach
    @SneakyThrows
    @SuppressWarnings("all")
    void setup() {
        sut = new AnnotationDependencyConfigurationScanner();
        var sutClass = sut.getClass();
        var targetClassesPathField = sutClass.getDeclaredField("targetClassesPath");
        targetClassesPathField.setAccessible(true);
        targetClassesPathField.set(sut, TEST_TARGET_PATH);
    }

    @Test
    void ifScanSuccessfulThenValidDependencyDefinitions() {
        var configurationsMap = sut.scan(TEST_PACKAGE);
        var stringDependencyDefinitions = configurationsMap.get("java.lang.String");
        var integerDependencyDefinitions = configurationsMap.get("java.lang.Integer");
        var personDependencyDefinitions = configurationsMap.get("com.bobocode.petros.testpackage.scan.config.TestPersonConfigClass$Person");
        var namedDependency = configurationsMap.get("com.bobocode.petros.testpackage.scan.config.TestConfigClass$NamedDependency");
        var personDependencyDefinition = personDependencyDefinitions.get(0);
        var personDependencyDefinitionName = personDependencyDefinition.getName();
        var personQualifiedName = personDependencyDefinition.getQualifiedName();
        var personInjectedDependencyDefinitions = personDependencyDefinition.getDependencyDefinitions();

        assertAll(
                () -> assertEquals(2, stringDependencyDefinitions.size()),
                () -> assertEquals(1, integerDependencyDefinitions.size()),
                () -> assertEquals(1, personDependencyDefinitions.size()),
                () -> assertEquals("namedDependency", namedDependency.get(0).getName()),
                () -> assertEquals("person", personDependencyDefinitionName),
                () -> assertEquals("com.bobocode.petros.testpackage.scan.config.TestPersonConfigClass$Person", personQualifiedName),
                () -> assertTrue(personInjectedDependencyDefinitions.stream().anyMatch(d -> d.getName().equals("name"))),
                () -> assertTrue(personInjectedDependencyDefinitions.stream().anyMatch(d -> d.getName().equals("lastName")))
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