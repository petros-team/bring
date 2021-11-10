package com.bobocode.petros.injector;

import com.bobocode.petros.container.DependencyDefinition;
import com.bobocode.petros.scaner.AnnotationDependencyClassScanner;
import com.bobocode.petros.scaner.AnnotationDependencyConfigurationScanner;
import com.bobocode.petros.scaner.DependencyScanner;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("all")
public class AnnotationDependencyInjector implements DependencyInjector {
    private final DependencyScanner classScanner;
    private final DependencyScanner configurationScanner;

    private final String packageName;
    private Map<DependencyDefinition, Object> dependencyMap = new HashMap<>();

    public AnnotationDependencyInjector(String packageName) {
        this.classScanner = new AnnotationDependencyClassScanner();
        this.configurationScanner = new AnnotationDependencyConfigurationScanner();
        this.packageName = packageName;
    }

    @Override
    public Map<DependencyDefinition, Object> injectedDependencyDefinitionObjectMap() {

//        var map1 = classScanner.scan(packageName);
//        var map2 = configurationScanner.scan(packageName);

        return null;
    }


}