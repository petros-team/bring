package com.bobocode.petros.container;

import com.bobocode.petros.exception.NoUniqueDependecyException;
import com.bobocode.petros.injector.AnnotationDependencyInjector;
import com.bobocode.petros.injector.DependencyInjector;

import java.util.Map;


public class ApplicationAnnotationContainer implements ApplicationContainer {
    private DependencyInjector dependencyInjector;
    private Map<DependencyDefinition, Object> dependencyMap;

    private ApplicationAnnotationContainer() {
    }

    public ApplicationAnnotationContainer(String packageName) {
        dependencyInjector = new AnnotationDependencyInjector(packageName);
        dependencyMap = dependencyInjector.injectedDependencyDefinitionObjectMap();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getDependency(String name, Class<T> clazz) {
        DependencyDefinition definition = keyByDependencyDefinitionName(name);
        return (T) dependencyMap.get(definition);
    }


    @Override
    public <T> T getDependency(Class<T> clazz) {
        if (isNonUniqueDependency(clazz)){
            throw new NoUniqueDependecyException(clazz.getName());
        } else {
            return getDependencyFromMap(clazz);
        }
    }

    private DependencyDefinition keyByDependencyDefinitionName(String name){
        return dependencyMap.keySet().stream()
                .filter(dependencyDefinition -> dependencyDefinition.getName().equals(name))
                .findFirst()
                .orElseThrow();
    }

    private <T> boolean isNonUniqueDependency(Class<T> clazz){
        return dependencyMap.values().stream()
                .filter(obj ->  obj.getClass().equals(clazz))
                .count() > 1;
    }

    @SuppressWarnings("unchecked")
    private <T> T getDependencyFromMap(Class<T> clazz){
        return (T) dependencyMap.values().stream()
                .filter(obj ->  obj.getClass().equals(clazz))
                .findFirst()
                .orElseThrow();
    }
}