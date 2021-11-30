package com.bobocode.petros.container;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A DependencyDefinition describes a dependency instance, its constructor or method argument values, and further
 * information supplied by concrete implementations.
 */
@Slf4j
@ToString
public class DependencyDefinition {
    private boolean configClassDependency;
    private String name;
    private String qualifiedName;
    private String injectedDependencyMethodName;
    private final Collection<DependencyDefinition> injectedDependencyDefinitions;

    @Getter
    @Setter
    private String configClassQualifiedName;

    public DependencyDefinition() {
        injectedDependencyDefinitions = new ArrayList<>();
    }

    /**
     * Return true in case if Dependency is declared inside configuration class that is marked with ConfigClass annotation
     */
    public boolean isConfigClassDependency() {
        return configClassDependency;
    }

    /**
     * Set the configClassDependency flag to true in case if Dependency is declared inside configuration class that is marked with ConfigClass annotation
     *
     * @param configClassDependency
     *        setting this parameter to true we indicate that the given class is a configuration Dependency
     */
    public void setConfigClassDependency(boolean configClassDependency) {
        this.configClassDependency = configClassDependency;
    }

    /**
     * Return the identification name of the given Dependency. By this name given Dependency can be retrieved from the Container
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the given Dependency. By this name given Dependency can be retrieved from the Container
     *
     * @param name
     *        identification name of the given Dependency
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return the Dependency qualified name that consists of the package that the Dependency class originated from and its name
     */
    public String getQualifiedName() {
        return qualifiedName;
    }

    /**
     * Set the Dependency qualified name that consists of the package that the Dependency class originated from and its name
     *
     * @param qualifiedName
     *        fully qualified name of the given Dependency
     */
    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    /**
     * Return the name of the method that constructs Dependency described by current DependencyDefinition
     */
    public String getConfigDependencyMethodName() {
        return injectedDependencyMethodName;
    }

    /**
     * Set the name of the method that constructs Dependency described by current DependencyDefinition.
     * In case if the Dependency is not configuration then this parameter must be null
     *
     * @param injectedDependencyMethodName
     *        name of the configuration method
     */
    public void setConfigDependencyMethodName(String injectedDependencyMethodName) {
        this.injectedDependencyMethodName = injectedDependencyMethodName;
    }

    /**
     * Add DependencyDefinition that given Dependency depends on
     *
     * @param dependencyQualifiedName
     *        DependencyDefinition that given Dependency depends on
     */
    public void addInjectedDependencyDefinition(DependencyDefinition dependencyQualifiedName) {
        injectedDependencyDefinitions.add(dependencyQualifiedName);
    }

    /**
     * Return a collection of all DependencyDefinitions that given Dependency depends on
     */
    public Collection<DependencyDefinition> getDependencyDefinitions() {
        return injectedDependencyDefinitions;
    }

}
