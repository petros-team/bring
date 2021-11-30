package com.bobocode.petros.scaner;

import com.bobocode.petros.annotation.ConfigClass;
import com.bobocode.petros.annotation.Dependency;
import com.bobocode.petros.container.DependencyDefinition;
import com.bobocode.petros.exception.DependencyClassNotFoundException;
import com.bobocode.petros.exception.NoSuchPackageFoundException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

@Slf4j
public class AnnotationDependencyConfigurationScanner implements DependencyScanner {
    @SuppressWarnings("FieldMayBeFinal")
    private String targetClassesPath = "target/classes/";
    private final String fileSeparator = System.getProperty("file.separator");

    @Override
    public Map<String, List<DependencyDefinition>> scan(String packageName) {
        LOG.info("Start scanning of package {} for configurations", packageName);
        var qualifiedNamesFromPackage = listOfQualifiedNamesFromPackage(packageName);
        LOG.debug("Package {} successfully scanned", packageName);
        var configClasses = listOfConfigClassesFromQualifiedNames(qualifiedNamesFromPackage);
        LOG.debug("Configurations classes found: [{}]", configClasses);
        var dependencyMethods = dependencyMethodsByConfigClasses(configClasses);
        LOG.debug("Dependency methods found: [{}]", configClasses);
        var configurationsMap = groupMethodsDependencyDefinitions(dependencyMethods);
        LOG.info("Package {} successfully scanned. Found DependendyDefinitions: [{}]", packageName, configurationsMap);
        return configurationsMap;
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
            LOG.error("Package name {} doesn't exist. Please check a parameter passed into container", packageName);
            throw new NoSuchPackageFoundException(packageName);
        }
    }

    private List<Class<?>> listOfConfigClassesFromQualifiedNames(List<String> qualifiedNames) {
        return qualifiedNames.stream()
                .map(this::getClassForName)
                .filter(c -> Objects.nonNull(c.getAnnotation(ConfigClass.class)))
                .collect(Collectors.toList());
    }

    private Map<String, List<Method>> dependencyMethodsByConfigClasses(List<Class<?>> configClasses) {
        return configClasses.stream()
                .collect(Collectors.toMap(Class::getName, this::getDependencyMethods));
    }

    private List<Method> getDependencyMethods(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Dependency.class))
                .collect(Collectors.toList());
    }

    private Map<String, List<DependencyDefinition>> groupMethodsDependencyDefinitions(Map<String, List<Method>> methods) {
        return methods.entrySet().stream()
                .map(this::dependencyDefinitionsFrom)
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(
                        DependencyDefinition::getQualifiedName,
                        Collectors.mapping(identity(), Collectors.toList())
                ));
    }

    private List<DependencyDefinition> dependencyDefinitionsFrom(Map.Entry<String, List<Method>> entry) {
        return entry.getValue().stream()
                .map(method -> getDependencyDefinitionFrom(entry, method))
                .collect(Collectors.toList());
    }

    private DependencyDefinition getDependencyDefinitionFrom(Map.Entry<String, List<Method>> entry, Method method) {
        var returnTypeDependencyDefinition = new DependencyDefinition();
        returnTypeDependencyDefinition.setName(getDependencyNameFrom(method));
        returnTypeDependencyDefinition.setConfigClassDependency(true);
        returnTypeDependencyDefinition.setConfigDependencyMethodName(method.getName());
        returnTypeDependencyDefinition.setQualifiedName(method.getReturnType().getName());
        returnTypeDependencyDefinition.setConfigClassQualifiedName(entry.getKey());
        setInjectedDependenciesDefinitionsFromParameters(returnTypeDependencyDefinition, method.getParameters());
        return returnTypeDependencyDefinition;
    }

    private String getDependencyNameFrom(Method method) {
        var dependencyAnnotation = method.getAnnotation(Dependency.class);
        var dependencyName = dependencyAnnotation.name();
        return dependencyName.isBlank() ? method.getName() : dependencyName;
    }

    private void setInjectedDependenciesDefinitionsFromParameters(DependencyDefinition returnTypeDependencyDefinition, Parameter[] parameters) {
        for (Parameter parameter : parameters) {
            var dependencyDefinition = new DependencyDefinition();
            dependencyDefinition.setName(parameter.getName());
            dependencyDefinition.setQualifiedName(parameter.getType().getName());
            returnTypeDependencyDefinition.addInjectedDependencyDefinition(dependencyDefinition);
        }
    }

    private Class<?> getClassForName(String qualifiedName) {
        try {
            return Class.forName(qualifiedName);
        } catch (ClassNotFoundException e) {
            throw new DependencyClassNotFoundException(e.getMessage(), e);
        }
    }
}
