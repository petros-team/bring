package com.bobocode.petros.injector;

import com.bobocode.petros.container.DependencyDefinition;

import java.util.Map;

/**
 * Creates instances of dependencies based on their {@link DependencyDefinition`s},
 * to be injected to {@link com.bobocode.petros.container.ApplicationContainer}
 */
public interface DependencyInjector {

    /**
     * Creates an instances of objects based on their {@link DependencyDefinition`s}
     *
     * @return {@link Map} where key is {@link DependencyDefinition} of dependency, and value is an instance created
     * based on provided {@link DependencyDefinition}
     */
    Map<DependencyDefinition, Object> injectedDependencyDefinitionObjectMap();
}
