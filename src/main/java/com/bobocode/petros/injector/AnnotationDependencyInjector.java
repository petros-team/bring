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

    public AnnotationDependencyInjector(String packageName) {
        this.classScanner = new AnnotationDependencyClassScanner();
        this.configurationScanner = new AnnotationDependencyConfigurationScanner();
        this.packageName = packageName;
    }

    @Override
    public Map<DependencyDefinition, Object> injectedDependencyDefinitionObjectMap() {
        Map<String, List<DependencyDefinition>> map1 = classScanner.scan(packageName);

//        Map<String, List<DependencyDefinition>> map2 = configurationScanner.scan(packageName);
        return null;
    }

    private Object createInstance(DependencyDefinition definition) {
        Object instance = definition.getDependencyClass();
        Set<DependencyDefinition> injectedDependencyDefinitions = definition.getDependencyDefinitions();

        if (!injectedDependencyDefinitions.isEmpty()) {
            for (DependencyDefinition dependency : injectedDependencyDefinitions) {
                findField(instance, dependency.getDependencyClass().getClass())
                        .ifPresent(field -> {
                            Object instanceToBeInjected = dependencyMap
                                    .computeIfAbsent(dependency, d -> createInstance(dependency));
                            setPriveteField(instance, field, instanceToBeInjected);
                        });
            }
        }
        return instance;
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

    private <T> Optional<Field> findField(T obj, Class<?> type) {
        return Arrays.stream(obj.getClass().getDeclaredFields())
                .filter(f -> f.getType().isInstance(type)).findFirst();
    }
}