package com.bobocode.petros.scaner;

import com.bobocode.petros.container.DependencyDefinition;

import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
public class AnnotationDependencyClassScanner implements DependencyScanner {
    public AnnotationDependencyClassScanner() {
    }

    @Override
    public Map<String, List<DependencyDefinition>> scan(String packageName) {
        return null;
    }
}
