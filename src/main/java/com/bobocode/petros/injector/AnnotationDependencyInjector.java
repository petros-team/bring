package com.bobocode.petros.injector;

import com.bobocode.petros.container.DependencyDefinition;
import com.bobocode.petros.scaner.AnnotationDependencyClassScanner;
import com.bobocode.petros.scaner.AnnotationDependencyConfigurationScanner;
import com.bobocode.petros.scaner.DependencyScanner;

import java.lang.reflect.Field;
import java.util.*;

@SuppressWarnings("all")
public class AnnotationDependencyInjector implements DependencyInjector {
    private final DependencyScanner classScanner;
    private final DependencyScanner configurationScanner;

    private final String packageName;
    private Map<DependencyDefinition, Object> dependencyMap = new HashMap<>();
    private Map<String, List<DependencyDefinition>> map1;


    public AnnotationDependencyInjector(String packageName) {
        this.classScanner = new AnnotationDependencyClassScanner();
        this.configurationScanner = new AnnotationDependencyConfigurationScanner();
        this.packageName = packageName;
    }

    @Override
    public Map<DependencyDefinition, Object> injectedDependencyDefinitionObjectMap() {
        this.map1 = classScanner.scan(packageName);
        map1.entrySet().stream().map(v -> v.getValue()).forEach(d -> d.forEach(dd -> createInstance(dd)));
        map1.entrySet().stream().map(v -> v.getValue()).forEach(d -> d.forEach(dd -> createInstance(dd)));

//        Map<String, List<DependencyDefinition>> map2 = configurationScanner.scan(packageName);
        return dependencyMap;
    }

    private Object createInstance(DependencyDefinition definition) {
        Object instance = definition.getDependencyClass();
        injectDependenciesFromDefinition(instance, definition);
        if (!containsDependencyByName(definition.getName())) {
            dependencyMap.put(definition, instance);
        }
        return instance;
    }

    private <T> T injectDependenciesFromDefinition(T instance, DependencyDefinition definition) {
        Collection<DependencyDefinition> injectedDependencyDefinitions = definition.getDependencyDefinitions();
        if (!injectedDependencyDefinitions.isEmpty()) {
            injectedDependencyDefinitions.forEach(dependency -> injectDependency(instance, dependency));
        }
        return instance;
    }

    private <T, R> void injectDependency(T instance, DependencyDefinition dependency) {
        Optional<Field> fieldToBeInjected = Optional.empty();

        if (map1.get(dependency.getQualifiedName()) != null && map1.get(dependency.getQualifiedName()).size() > 1) {
            findField(instance,
                    dependency.getDependencyClass().getClass(), dependency.getName());
        } else {
            findField(instance,
                    dependency.getDependencyClass().getClass());
        }
        if (fieldToBeInjected.isPresent()) {
            if (!containsDependencyByName(dependency.getName())) {
                dependencyMap.put(dependency, createInstance(dependency));
            }
            setPriveteField(instance, fieldToBeInjected.get(), dependencyMap.get(dependency));
        }
    }


    private <T> void setPriveteField(T obj, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(obj, value);
            field.setAccessible(false);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private boolean containsDependencyByName(final String name) {
        return dependencyMap.keySet().stream()
                .anyMatch(d -> name.equals(d.getName()));
    }


    private <T> Optional<Field> findField(T obj, Class<?> type) {
        return Arrays.stream(obj.getClass().getDeclaredFields())
                .filter(f -> f.getType().getName().equals(type.getName())).findFirst();
    }

    private <T> Optional<Field> findField(T obj, Class<?> type, String name) {
        return Arrays.stream(obj.getClass().getDeclaredFields())
                .filter(f -> f.getType().getName().equals(type.getName())
                        && f.getName().equals(name)).findFirst();
    }
}