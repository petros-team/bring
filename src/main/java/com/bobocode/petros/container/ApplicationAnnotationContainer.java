package com.bobocode.petros.container;

import com.bobocode.petros.exception.NoUniqueDependecyException;
import com.bobocode.petros.injector.AnnotationDependencyInjector;
import com.bobocode.petros.injector.DependencyInjector;

import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;

@SuppressWarnings("all")
public class ApplicationAnnotationContainer implements ApplicationContainer {
    private DependencyInjector dependencyInjector;
    private Map<DependencyDefinition, Object> dependencyMap;

    public ApplicationAnnotationContainer(String packageName) {
        dependencyInjector = new AnnotationDependencyInjector(packageName);
        dependencyMap = dependencyInjector.injectedDependencyDefinitionObjectMap();
    }

    @Override
    public <T> T getDependency(String name, Class<T> clazz) {
        return (T) dependencyMap.values().stream()
                .filter(obj -> obj.getClass().getName().toLowerCase().equals(name.toLowerCase())
                        && obj.getClass().equals(clazz))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No such dependency in container"));

    }

    @Override
    public <T> T getDependency(Class<T> clazz) {
        if (!isNonUniqueDependency(clazz)){
            return getDependencyFromMap(clazz);
        } else {
            throw new NoUniqueDependecyException(clazz.getName());
        }
    }

    private <T> boolean isNonUniqueDependency(Class<T> clazz){
        return dependencyMap.values().stream()
                .filter(obj ->  obj.getClass().equals(clazz))
                .count() > 1;
    }

    private <T> T getDependencyFromMap(Class<T> clazz){
        return (T) dependencyMap.values().stream()
                .filter(obj ->  obj.getClass().equals(clazz))
                .findFirst();
    }
}