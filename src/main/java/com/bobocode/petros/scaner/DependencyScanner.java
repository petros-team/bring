package com.bobocode.petros.scaner;

import com.bobocode.petros.container.DependencyDefinition;

import java.util.List;
import java.util.Map;

/**
 * Central interface to provide scan for an application
 * and create for user instances of {@link DependencyDefinition's}
 * Purpose this interface add api that will help create
 * instance class without operator "new" and user could use
 * them as simple class.
 */

public interface DependencyScanner {

    /**
     * Scan and creates an instance of {@link DependencyDefinition}
     * by packageName.
     *
     * @param packageName path to the package that has be scan.
     * @return {@link Map} where key is {@link String} class name of dependency,
     * and values are an instances of {@link DependencyDefinition's}
     */

    Map<String, List<DependencyDefinition>> scan(String packageName);
}
