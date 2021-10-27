package com.bobocode.petros.injector;

import com.bobocode.petros.container.DependencyDefinition;
import com.bobocode.petros.scaner.AnnotationDependencyClassScanner;
import com.bobocode.petros.scaner.AnnotationDependencyConfigurationScanner;
import com.bobocode.petros.scaner.DependencyScanner;

import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
public class AnnotationDependencyInjector implements DependencyInjector {
    private final DependencyScanner classScanner;
    private final DependencyScanner configurationScanner;

    public AnnotationDependencyInjector(String packageName) {
        classScanner = new AnnotationDependencyClassScanner(packageName);
        configurationScanner = new AnnotationDependencyConfigurationScanner(packageName);
    }

    @Override
    public Map<DependencyDefinition, Object> injectedDependencyDefinitionObjectMap(Map<String, List<DependencyDefinition>> dependenciesMap) {
        return null;
    }
}
