package com.bobocode.petros.scaner;

import com.bobocode.petros.annotation.Dependency;
import com.bobocode.petros.annotation.Injected;
import com.bobocode.petros.exception.DefaultConstructorNotFoundException;
import com.bobocode.petros.exception.DependencyClassNotFoundException;
import com.bobocode.petros.exception.MultipleInjectConstructorsException;
import com.bobocode.petros.exception.NoSuchPackageFoundException;
import com.bobocode.petros.exception.NoSuchPathFoundException;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.bobocode.petros.container.DependencyDefinition;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

@Slf4j
public class AnnotationDependencyClassScanner implements DependencyScanner {

    private static final int COUNT_OF_PARAMETER_IN_CONSTRUCTOR = 0;
    @SuppressWarnings("FieldMayBeFinal")
    private String targetClassesPath = "target/classes/";
    @SuppressWarnings("FieldMayBeFinal")
    private boolean isDebugMode = false;
    private String strictPath;

    public AnnotationDependencyClassScanner() {
    }

    @Override
    public Map<String, List<DependencyDefinition>> scan(String pathPackage) {
        var path = Paths.get(targetClassesPath + pathPackage.replace(".", "/"));
        LOG.info("Start scanning of package {} for configurations", path);
        var walk = getPathStream(path);
        LOG.debug("Package {} successfully scanned", path);
        return walk
                .filter(Files::isRegularFile)
                .map(this::testMode)
                .map(this::getClassByPath)
                .filter(this::hasClassMarkedAsDependency)
                .filter(this::hasDefaultConstructor)
                .filter(this::hasOnlyOneConstructorMarkedAsInject)
                .peek(s -> LOG.info("Package {} successfully scanned. Found Dependency Definitions: [{}]", pathPackage, s.getSimpleName()))
                .collect(groupingBy(Class::getName,
                        mapping(this::getDependencyDefinition, toList())));
    }

    private Path testMode(Path path) {
        if (isDebugMode) {
            return Paths.get(targetClassesPath, strictPath);
        }
        return path;
    }

    private boolean hasOnlyOneConstructorMarkedAsInject(Class<?> aClass) {
        var hasMoreOneInjectedConstructor = Arrays.stream(aClass.getDeclaredConstructors())
                .filter(a -> a.isAnnotationPresent(Injected.class))
                .count() >= 2;
        if (hasMoreOneInjectedConstructor) {
            LOG.error("Class {} has more that one Injected constructors", aClass.getName());
            throw new MultipleInjectConstructorsException(aClass.getName());
        }
        return true;
    }

    private DependencyDefinition getDependencyDefinition(Class<?> aClass) {
        var dependencyDefinition = new DependencyDefinition();
        dependencyDefinition.setName(firstCharacterToLowercase(aClass.getSimpleName()));
        dependencyDefinition.setQualifiedName(aClass.getName());
        dependencyDefinitionConstructorArgs(aClass)
                .peek(ourClass -> LOG.debug("In constructor {} was initialized class - {}", aClass.getSimpleName(), ourClass.getQualifiedName()))
                .forEach(dependencyDefinition::addInjectedDependencyDefinition);
        return dependencyDefinition;
    }

    private Stream<DependencyDefinition> dependencyDefinitionConstructorArgs(Class<?> aClass) {
        return getConstructorsParam(aClass).stream()
                .filter(p->hasDefaultConstructorByClass(p.getType()))
                .map(this::createInnerDependencyDefinition);
    }

    private boolean hasDefaultConstructorByClass(Class<?> aClass) {
        var hasDefConstructor = Arrays.stream(aClass.getDeclaredConstructors())
                .anyMatch(a -> a.getParameterCount() == COUNT_OF_PARAMETER_IN_CONSTRUCTOR);
        if (!hasDefConstructor) {
            LOG.error("Class " + aClass.getSimpleName() + " must have default constructor class or this class must be created in config class");
            throw new DefaultConstructorNotFoundException(aClass.getSimpleName());
        }
        return true;
    }

    private DependencyDefinition createInnerDependencyDefinition(Parameter parameter) {
        var definition = new DependencyDefinition();
        definition.setName(parameter.getName());
        definition.setQualifiedName(parameter.getType().getName());
        return definition;
    }

    private List<Parameter> getConstructorsParam(Class<?> aClass) {
        return Arrays.stream(aClass.getDeclaredConstructors())
                .flatMap(a -> Arrays.stream(a.getParameters()))
                .collect(toList());
    }

    private Stream<Path> getPathStream(Path path) {
        try {
            return Files.walk(path);
        } catch (IOException e) {
            LOG.error("Package {} unsuccessfully scanned", getSimpleClassPathName(path));
            throw new NoSuchPackageFoundException(getSimpleClassPathName(path));
        }
    }

    private boolean hasDefaultConstructor(Class<?> aClass) {
        var hasDefaultConstructor = Arrays.stream(aClass.getDeclaredConstructors())
                .anyMatch(a -> a.getParameterCount() == COUNT_OF_PARAMETER_IN_CONSTRUCTOR);
        if (!hasDefaultConstructor) {
            LOG.error("Class {} doesn't have default constructor", aClass.getName());
            throw new DefaultConstructorNotFoundException(aClass.getName());
        }
        return true;
    }

    private boolean hasClassMarkedAsDependency(Class<?> path) {
        return path.isAnnotationPresent(Dependency.class);
    }

    private Class<?> getClassByPath(Path path) {
        try {
            return Class.forName(getSimpleClassPathName(path));
        } catch (ClassNotFoundException e) {
            throw new DependencyClassNotFoundException(getSimpleClassPathName(path));
        }
    }

    private static String getSimpleClassPathName(Path path) {
        try {
            return path.toString().replace("/", ".")
                    .substring(path.toString().indexOf("com"))
                    .replace(".class", "");
        } catch (IndexOutOfBoundsException e) {
            throw new NoSuchPathFoundException(path.toString());
        }
    }

    private String firstCharacterToLowercase(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

}