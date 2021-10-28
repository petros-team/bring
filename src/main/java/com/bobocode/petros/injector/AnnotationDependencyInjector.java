package com.bobocode.petros.injector;

import com.bobocode.petros.container.DependencyDefinition;
import com.bobocode.petros.scaner.AnnotationDependencyClassScanner;
import com.bobocode.petros.scaner.AnnotationDependencyConfigurationScanner;
import com.bobocode.petros.scaner.DependencyScanner;


import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("all")
public class AnnotationDependencyInjector implements DependencyInjector {
    private final DependencyScanner classScanner;
    private final DependencyScanner configurationScanner;

    private final String packageName;
    private ConcurrentHashMap<DependencyDefinition, Object> dependencyMap = new ConcurrentHashMap<>();

    public AnnotationDependencyInjector(String packageName) {
        this.classScanner = new AnnotationDependencyClassScanner();
        this.configurationScanner = new AnnotationDependencyConfigurationScanner();
        this.packageName = packageName;
    }

    @Override
    public ConcurrentHashMap<DependencyDefinition, Object> injectedDependencyDefinitionObjectMap() {
        var map1 = classScanner.scan(packageName);
        var map2 = configurationScanner.scan(packageName);
        return null;
    }



}