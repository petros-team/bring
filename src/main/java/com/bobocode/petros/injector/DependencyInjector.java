package com.bobocode.petros.injector;

import com.bobocode.petros.container.DependencyDefinition;

import java.util.List;
import java.util.Map;

public interface DependencyInjector {
    Map<DependencyDefinition, Object> injectedDependencyDefinitionObjectMap(Map<String, List<DependencyDefinition>> dependenciesMap);
}
