package com.bobocode.petros.scaner;

import com.bobocode.petros.container.DependencyDefinition;

import java.util.List;
import java.util.Map;

public interface DependencyScanner {
    Map<String, List<DependencyDefinition>> scan(String packageName);
}
