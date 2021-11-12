package com.bobocode.petros.container;

import com.bobocode.petros.exception.NoDefaultConstructorException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Slf4j
@ToString
public class DependencyDefinition {
    private boolean configClassDependency;
    private String name;
    private String qualifiedName;
    private String injectedDependencyMethodName;
    private final Collection<DependencyDefinition> injectedDependencyDefinitions;
    private Object dependency;
    @Getter
    @Setter
    private String configClassQualifiedName;

    public DependencyDefinition() {
        injectedDependencyDefinitions = new ArrayList<>();
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

    public boolean isConfigClassDependency() {
        return configClassDependency;
    }

    public void setConfigClassDependency(boolean configClassDependency) {
        this.configClassDependency = configClassDependency;
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

    public String getInjectedDependencyMethodName() {
        return injectedDependencyMethodName;
    }

    public void setInjectedDependencyMethodName(String injectedDependencyMethodName) {
        this.injectedDependencyMethodName = injectedDependencyMethodName;
    }

    public void addInjectedDependencyDefinition(DependencyDefinition dependencyQualifiedName) {
        injectedDependencyDefinitions.add(dependencyQualifiedName);
    }

    public Collection<DependencyDefinition> getDependencyDefinitions() {
        return injectedDependencyDefinitions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DependencyDefinition that = (DependencyDefinition) o;
        return isConfigClassDependency() == that.isConfigClassDependency() && getName().equals(that.getName()) && getQualifiedName().equals(that.getQualifiedName()) && Objects.equals(getInjectedDependencyMethodName(), that.getInjectedDependencyMethodName()) && Objects.equals(injectedDependencyDefinitions, that.injectedDependencyDefinitions) && Objects.equals(dependency, that.dependency) && Objects.equals(getConfigClassQualifiedName(), that.getConfigClassQualifiedName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(isConfigClassDependency(), getName(), getQualifiedName(), getInjectedDependencyMethodName(), injectedDependencyDefinitions, dependency, getConfigClassQualifiedName());
    }
}
