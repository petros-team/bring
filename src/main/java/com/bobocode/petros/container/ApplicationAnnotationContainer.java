package com.bobocode.petros.container;

import com.bobocode.petros.exception.NoUniqueDependencyException;
import com.bobocode.petros.injector.AnnotationDependencyInjector;
import com.bobocode.petros.injector.DependencyInjector;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@NoArgsConstructor
public class ApplicationAnnotationContainer implements ApplicationContainer {
    private DependencyInjector dependencyInjector;
    /**
     * main repository of dependencies instances
     */
    private Map<DependencyDefinition, Object> dependencyMap;

    public ApplicationAnnotationContainer(String packageName) {
        LOG.info("Creating instance of dependencyInjector and passing the package name {} for scanning", packageName);
        try {
            dependencyInjector = new AnnotationDependencyInjector(packageName);
            dependencyMap = dependencyInjector.injectedDependencyDefinitionObjectMap();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        LOG.info("Successfully received map of dependencies from dependencyInjector");
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T> T getDependency(String name, Class<T> clazz) {
        LOG.debug("Searching dependency by name {} and by type {}", name, clazz);
        DependencyDefinition definition = new DependencyDefinition();
        try {
            definition = keyByDependencyDefinitionName(name);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        LOG.debug("Dependency definition found {}", definition);
        return (T) dependencyMap.get(definition);
    }


    @Override
    public <T> T getDependency(Class<T> clazz) {
        T dependency = null;
        try {
            if (isNonUniqueDependency(clazz)) {
                throw new NoUniqueDependencyException(clazz.getName());
            }
            dependency = getDependencyFromMap(clazz);
        } catch (NoUniqueDependencyException | NoSuchElementException e) {
            LOG.error(e.getMessage(), e);
        }
        return dependency;
    }

    private DependencyDefinition keyByDependencyDefinitionName(String name) {
        return dependencyMap.keySet().stream()
                .filter(dependencyDefinition -> dependencyDefinition.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("There is no DependencyDefinition for %s", name))
                );
    }

    private <T> boolean isNonUniqueDependency(Class<T> clazz) {
        return dependencyMap.values().stream()
                .filter(obj -> obj.getClass().equals(clazz))
                .count() > 1;
    }

    @SuppressWarnings("unchecked")
    private <T> T getDependencyFromMap(Class<T> clazz) {
        return (T) dependencyMap.values().stream()
                .filter(obj -> obj.getClass().equals(clazz))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("There is no instance of %s", clazz.getSimpleName()))
                );
    }
}