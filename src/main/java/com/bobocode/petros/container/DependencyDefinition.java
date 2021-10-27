package com.bobocode.petros.container;

import com.bobocode.petros.exception.NoDefaultConstructorException;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@ToString
public class DependencyDefinition {
    private String name;
    private String qualifiedName;
    private final Set<DependencyDefinition> injectedDependencyDefinitions;
    private static Object dependency;

    public DependencyDefinition() {
        injectedDependencyDefinitions = new HashSet<>();
    }

    public Object getDependencyClass() {
        if (dependency == null) {
            try {
                dependency = Class.forName(qualifiedName).getConstructor().newInstance();
            } catch (Exception e) {
                LOG.error("No default constructor found for class {}", qualifiedName);
                throw new NoDefaultConstructorException(qualifiedName);
            }
        }
        return dependency;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public void addInjectedDependencyDefinition(DependencyDefinition dependencyQualifiedName) {
        injectedDependencyDefinitions.add(dependencyQualifiedName);
    }

    public Set<DependencyDefinition> getDependencyDefinitions() {
        return injectedDependencyDefinitions;
    }

}
