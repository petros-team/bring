package com.bobocode.petros.scaner;

import com.bobocode.petros.annotation.Dependency;
import com.bobocode.petros.annotation.Injected;
import com.bobocode.petros.container.DependencyDefinition;
import com.bobocode.petros.exception.DefaultConstructorNotFoundException;
import com.bobocode.petros.exception.DependencyClassNotFoundException;
import com.bobocode.petros.exception.MultipleInjectConstructorsException;
import com.bobocode.petros.exception.NoSuchPackageFoundException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class AnnotationDependencyClassScanner implements DependencyScanner {
    private static final int NUMBER_OF_PARAMETERS_IN_DEFAULT_CONSTRUCTOR = 0;

    @SuppressWarnings("FieldMayBeFinal")
    private String targetClassesPath = "target/classes/";
    private final String fileSeparator = System.getProperty("file.separator");


    @Override
    public Map<String, List<DependencyDefinition>> scan(String packageName) {
        LOG.info("Start scanning of package [{}] for dependency classes", packageName);
        var qualifiedNamesFromPackage = listOfQualifiedNamesFromPackage(packageName);
        LOG.debug("Package {} successfully scanned", packageName);
        var dependencyClasses = listOfDependencyClassesFromQualifiedNames(qualifiedNamesFromPackage);
        LOG.debug("Dependency classes found: [{}]", dependencyClasses);
        var classDependenciesMap = dependencyClasses.stream()
                .map(this::dependencyDefinitionFrom)
                .collect(Collectors.groupingBy(DependencyDefinition::getQualifiedName,
                        Collectors.mapping(Function.identity(), Collectors.toList())));
        LOG.info("Package {} successfully scanned. Found DependencyDefinitions: [{}]", packageName, classDependenciesMap);
        return classDependenciesMap;
    }

    private DependencyDefinition dependencyDefinitionFrom(Class<?> aClass) {
        var dependencyDefinition = new DependencyDefinition();
        dependencyDefinition.setName(firstCharacterToLowercase(aClass.getSimpleName()));
        dependencyDefinition.setQualifiedName(aClass.getName());
        setInjectedDependenciesDefinitions(dependencyDefinition, aClass);
        return dependencyDefinition;
    }

    private void setInjectedDependenciesDefinitions(DependencyDefinition dependencyDefinition, Class<?> aClass) {
        Arrays.stream(aClass.getDeclaredConstructors())
                .filter(a -> a.isAnnotationPresent(Injected.class))
                .flatMap(constructor -> Arrays.stream(constructor.getParameters()))
                .map(this::toInjectedDependencyDefinition)
                .peek(dependency -> LOG.debug("Injected dependency [{}] for [{}]",dependency.getQualifiedName(),aClass.getName() ))
                .forEach(dependencyDefinition::addInjectedDependencyDefinition);
    }

    private DependencyDefinition toInjectedDependencyDefinition(Parameter parameter) {
        var dependencyDefinition = new DependencyDefinition();
        LOG.debug("Creating Dependency Definition from parameter [{}]",parameter);
        dependencyDefinition.setQualifiedName(parameter.getType().getName());
        dependencyDefinition.setName(parameter.getName());
        return dependencyDefinition;
    }

    private String firstCharacterToLowercase(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    private List<String> listOfQualifiedNamesFromPackage(String packageName) {
        var packagePath = packageName.replace('.', '/');
        var path = Paths.get(targetClassesPath + packagePath);
        try (var files = Files.walk(path)) {
            return files
                    .filter(Files::isRegularFile)
                    .map(p -> p.toString().substring(targetClassesPath.length()))
                    .map(p -> p.replace(fileSeparator, "."))
                    .map(p -> p.substring(0, p.indexOf(".class")))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new NoSuchPackageFoundException(packageName, e);
        }
    }

    private List<Class<?>> listOfDependencyClassesFromQualifiedNames(List<String> qualifiedNames) {
        return qualifiedNames.stream()
                .map(this::getClassForName)
                .filter(c -> Objects.nonNull(c.getAnnotation(Dependency.class)))
                .filter(this::isValidDependencyClass)
                .collect(Collectors.toList());
    }

    private boolean isValidDependencyClass(Class<?> aClass) {
        return hasDefaultConstructor(aClass) && hasOnlyOneConstructorMarkedAsInject(aClass);
    }

    private boolean hasDefaultConstructor(Class<?> aClass) {
        var hasDefaultConstructor = Arrays.stream(aClass.getDeclaredConstructors())
                .anyMatch(a -> a.getParameterCount() == NUMBER_OF_PARAMETERS_IN_DEFAULT_CONSTRUCTOR);
        if (!hasDefaultConstructor) {
            throw new DefaultConstructorNotFoundException(aClass.getName());
        }
        return true;
    }

    private boolean hasOnlyOneConstructorMarkedAsInject(Class<?> aClass) {
        var hasMoreOneInjectedConstructor = Arrays.stream(aClass.getDeclaredConstructors())
                .filter(a -> a.isAnnotationPresent(Injected.class))
                .count() > 1;
        if (hasMoreOneInjectedConstructor) {
            throw new MultipleInjectConstructorsException(aClass.getName());
        }
        return true;
    }

    private Class<?> getClassForName(String qualifiedName) {
        try {
            return Class.forName(qualifiedName);
        } catch (ClassNotFoundException e) {
            throw new DependencyClassNotFoundException(e.getMessage(), e);
        }
    }

}