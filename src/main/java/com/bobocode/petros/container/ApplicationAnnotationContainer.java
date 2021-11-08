package com.bobocode.petros.container;

import com.bobocode.petros.exception.NoUniqueDependecyException;
import com.bobocode.petros.injector.AnnotationDependencyInjector;
import com.bobocode.petros.injector.DependencyInjector;

import java.util.Map;
import java.util.NoSuchElementException;


public class ApplicationAnnotationContainer implements ApplicationContainer {
    private DependencyInjector dependencyInjector;
    private final Map<DependencyDefinition, Object> dependencyMap;

    public ApplicationAnnotationContainer(String packageName) {
        dependencyInjector = new AnnotationDependencyInjector(packageName);
        dependencyMap = dependencyInjector.injectedDependencyDefinitionObjectMap();
    }

    @Override
    public <T> T getDependency(String name, Class<T> clazz) {
        DependencyDefinition definition = keyByDependencyDefinitionName(name);
        return (T) dependencyMap.values().stream()
                .filter(obj -> obj.getClass().getName().toLowerCase().equals(name.toLowerCase())
                        && obj.getClass().equals(clazz))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }


    @Override
    public <T> T getDependency(Class<T> clazz) {
        if (isUniqueDependency(clazz)){
            return getDependencyFromMap(clazz);
        } else {
            throw new NoUniqueDependecyException(clazz.getName());
        }
    }

    private DependencyDefinition keyByDependencyDefinitionName(String name){
        return dependencyMap.keySet().stream()
                .filter(dependencyDefinition -> dependencyDefinition.getName().equals(name))
                .findFirst()
                .orElseThrow();

    }

    private <T> boolean isUniqueDependency(Class<T> clazz){
        return dependencyMap.values().stream()
                .filter(obj ->  obj.getClass().equals(clazz))
                .count() == 1;
    }

    private <T> T getDependencyFromMap(Class<T> clazz){
        return (T) dependencyMap.values().stream()
                .filter(obj ->  obj.getClass().equals(clazz))
                .findFirst();
    }
}