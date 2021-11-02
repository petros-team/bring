package com.bobocode.petros.scaner;

import com.bobocode.petros.container.DependencyDefinition;
import com.bobocode.petros.exception.DefaultConstructorNotFoundException;
import com.bobocode.petros.exception.MultipleInjectConstructorsException;
import com.bobocode.petros.exception.NoSuchPackageFoundException;
import com.bobocode.petros.testpackage.scan.dependency.SuccessfullyBuildDefinitionDependencyCase;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

class AnnotationDependencyClassScannerTest {

    private static final String TESTING_TEST_CLASSES_PATH = "target/test-classes/";
    private static final String WRONG_PACKAGE_PATH = "com.bobocode.petros.wrong";
    private static final String CORRECT_PACKAGE_PATH = "com.bobocode.petros.testpackage.scan.dependency";

    private AnnotationDependencyClassScanner sut;

    @BeforeEach
    @SneakyThrows
    void setup() {
        sut = new AnnotationDependencyClassScanner();
        var aClass = sut.getClass();
        var isDebugMode = aClass.getDeclaredField("isDebugMode");
        isDebugMode.setAccessible(true);
        isDebugMode.set(sut, true);
        var targetClassesPath = aClass.getDeclaredField("targetClassesPath");
        targetClassesPath.setAccessible(true);
        targetClassesPath.set(sut, TESTING_TEST_CLASSES_PATH);
    }

    @Test
    void shouldThrowNoSuchPackageFoundExceptionWhenPathIsIncorrect() {
        var expectedMessage = String.format("No package with name = %s found", WRONG_PACKAGE_PATH);
        var noSuchPackageFoundException =
                Assertions.assertThrows(NoSuchPackageFoundException.class, () -> sut.scan(WRONG_PACKAGE_PATH));
        Assertions.assertEquals(expectedMessage, noSuchPackageFoundException.getMessage());
    }

    @Test
    @SneakyThrows
    void shouldThrowMultipleInjectConstructorsExceptionWhenClassHasTwoConstructorsMarkedInject() {
        var strictClassPath = "com.bobocode.petros.testpackage.scan.dependency.TwoConstructorsMarkedInjectedCase";
        setUpField(sut.getClass(), strictClassPath);
        var expectedMessage = String.format("Class %s has more that one constructor with @Inject annotation", strictClassPath);
        var multipleInjectConstructorsException =
                Assertions.assertThrows(MultipleInjectConstructorsException.class, () -> sut.scan(CORRECT_PACKAGE_PATH));
        Assertions.assertEquals(expectedMessage, multipleInjectConstructorsException.getMessage());
    }

    @Test
    void shouldThrowDefaultConstructorNotFoundExceptionWhenClassDoesntHaveDefaultConstructor() {
        var strictClassPath = "com.bobocode.petros.testpackage.scan.dependency.DontHaveDefaultConstructorCase";
        setUpField(sut.getClass(), strictClassPath);
        var expectedMessage = String.format("Class %s doesn't have default constructor", strictClassPath);
        var defaultConstructorNotFoundException =
                Assertions.assertThrows(DefaultConstructorNotFoundException.class, () -> sut.scan(CORRECT_PACKAGE_PATH).get(strictClassPath));
        Assertions.assertEquals(expectedMessage, defaultConstructorNotFoundException.getMessage());
    }

    @Test
    void shouldThrowDefaultConstructorNotFoundExceptionWhenConstructorHasParamClassWithoutDefaultConstructor() {
        var strictClassPath = "com.bobocode.petros.testpackage.scan.dependency.ConstructorHasParamClassWithoutDefaultConstructorCase";
        setUpField(sut.getClass(), strictClassPath);
        var expectedMessage = String.format("Class %s doesn't have default constructor", "TestClass");
        var defaultConstructorNotFoundException =
                Assertions.assertThrows(DefaultConstructorNotFoundException.class, () -> sut.scan(CORRECT_PACKAGE_PATH));
        Assertions.assertEquals(expectedMessage, defaultConstructorNotFoundException.getMessage());
    }

    @Test
    void shouldBeDependencyDefinition() {
        var strictPathName = "com.bobocode.petros.testpackage.scan.dependency.SuccessfullyBuildDefinitionDependencyCase";
        setUpField(sut.getClass(), strictPathName);
        var scan = sut.scan(CORRECT_PACKAGE_PATH);
        var actual = scan.entrySet().stream()
                .flatMap(a -> a.getValue().stream())
                .anyMatch(isPresentDependencyDefinition());
        Assertions.assertTrue(actual);
    }

    private Predicate<DependencyDefinition> isPresentDependencyDefinition() {
        return dependencyDefinition -> dependencyDefinition.getName().equals("successfullyBuildDefinitionDependencyCase") &&
                dependencyDefinition.getQualifiedName().equals("com.bobocode.petros.testpackage.scan.dependency.SuccessfullyBuildDefinitionDependencyCase") &&
                dependencyDefinition.getDependencyClass().getClass().equals(SuccessfullyBuildDefinitionDependencyCase.class);
    }

    @SneakyThrows
    private void setUpField(Class<? extends AnnotationDependencyClassScanner> aClass, String value) {
        var strictPath = aClass.getDeclaredField("strictPath");
        strictPath.setAccessible(true);
        strictPath.set(sut, value);
    }
}