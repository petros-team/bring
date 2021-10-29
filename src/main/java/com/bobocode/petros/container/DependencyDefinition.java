package com.bobocode.petros.container;

import java.util.Set;

@SuppressWarnings("all")
public class DependencyDefinition {
    private String name;
    private String qualifiedName;
    private Set<DependencyDefinition> injectedDependencyDefinitions;
    private static Object dependency;

    @SuppressWarnings("unchecked")
    public <T> T getDependencyClass() throws Exception {
        if (dependency == null) {
            dependency = (T) Class.forName(qualifiedName).getConstructor().newInstance();
        }
        return (T) dependency;
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