package com.bobocode.petros.container;

import com.bobocode.petros.exception.NoUniqueDependecyException;
import com.bobocode.petros.injector.AnnotationDependencyInjector;
import com.bobocode.petros.injector.DependencyInjector;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class ApplicationAnnotationContainer implements ApplicationContainer {
    private DependencyInjector dependencyInjector;
    /**
     * main repository of dependencies instances
     */
    private Map<DependencyDefinition, Object> dependencyMap;

    private ApplicationAnnotationContainer() {
    }


    public ApplicationAnnotationContainer(String packageName) {
        LOG.info("Creating instance of dependencyInjector and passing the package name {} for scanning", packageName);
        try {
            dependencyInjector = new AnnotationDependencyInjector(packageName);
            dependencyMap = dependencyInjector.injectedDependencyDefinitionObjectMap();
        } catch (Exception e) {
            LOG.debug("Can't start class scanner and dependency injector");
            e.printStackTrace();

        }
        LOG.info("Successfully received map of dependencies from dependencyInjector");
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T> T getDependency(String name, Class<T> clazz) {
        LOG.debug("Searching dependency by name {} and by type {}", name, clazz);
        DependencyDefinition definition = keyByDependencyDefinitionName(name);
        LOG.debug("Dependency definition found {}", definition);
        return (T) dependencyMap.get(definition);
    }


    @Override
    public <T> T getDependency(Class<T> clazz) {
        if (isNonUniqueDependency(clazz)){
            LOG.debug("The dependency with such {} type already exists in container",clazz.getName());
            LOG.info("Please try to use name");
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