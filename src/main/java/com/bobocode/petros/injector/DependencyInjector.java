package com.bobocode.petros.injector;

import com.bobocode.petros.container.DependencyDefinition;


import java.util.concurrent.ConcurrentHashMap;

public interface DependencyInjector {
    ConcurrentHashMap<DependencyDefinition, Object> injectedDependencyDefinitionObjectMap();
}
