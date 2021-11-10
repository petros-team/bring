package com.bobocode.petros.injector;

import com.bobocode.petros.container.DependencyDefinition;
import com.bobocode.petros.exception.InstanceInjectionException;
import com.bobocode.petros.scaner.AnnotationDependencyClassScanner;
import com.bobocode.petros.scaner.AnnotationDependencyConfigurationScanner;
import com.bobocode.petros.scaner.DependencyScanner;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author Andriy Kovalchuk
 * @author Demian Podolian
 * @author Andriy Paliychuk
 * @author to be continued...
 */
@Slf4j
@SuppressWarnings("all")
public class AnnotationDependencyInjector implements DependencyInjector {
    private final DependencyScanner classScanner;
    private final DependencyScanner configurationScanner;

    private final String packageName;
    private final Map<DependencyDefinition, Object> dependencyMap = new HashMap<>();

    public AnnotationDependencyInjector(String packageName) {
        this.classScanner = new AnnotationDependencyClassScanner();
        this.configurationScanner = new AnnotationDependencyConfigurationScanner();
        this.packageName = packageName;
    }

    @Override
    public Map<DependencyDefinition, Object> injectedDependencyDefinitionObjectMap() {
//        var map1 = classScanner.scan(packageName);
        var map2 = configurationScanner.scan(packageName);
        createConfigClassDependencies(map2);
        return dependencyMap;
    }

    private void createConfigClassDependencies(Map<String, List<DependencyDefinition>> scannedDependencies) {
        getDefinitionsSortedByArgsQuantity(scannedDependencies).stream()
                .filter(d -> !containsDependencyByName(d.getName()))
                .forEach(this::injectDependency);
    }

    private List<DependencyDefinition> getDefinitionsSortedByArgsQuantity(
            Map<String, List<DependencyDefinition>> scannedDependencies
    ) {
        return scannedDependencies.entrySet().stream()
                .flatMap(e -> e.getValue().stream())
                .sorted((d1, d2) -> d1.getDependencyDefinitions().size() - d2.getDependencyDefinitions().size())
                .collect(Collectors.toList());
    }

    private void injectDependency(DependencyDefinition definition) {
        var injectedDependencies = definition.getDependencyDefinitions();
        if (definition.isConfigClassDependency()) {
            injectDependency(definition, injectedDependencies);
        }
    }

    private void injectDependency(DependencyDefinition definition,
                                  Collection<DependencyDefinition> injectedDefinitions) {
        if (isReadyToCreateDependency(injectedDefinitions)) {
            dependencyMap.put(definition, createInjectedInstance(definition));
        } else {
            injectDependency(getAbsentDefinition(injectedDefinitions));
        }
    }

    private DependencyDefinition getAbsentDefinition(Collection<DependencyDefinition> injectedDependencies) {
        final DependencyDefinition absentDependency = injectedDependencies.stream()
                .filter(d -> !containsDependencyByName(d.getName()))
                .findFirst()
                .orElseThrow();
        return absentDependency;
    }

    private boolean isReadyToCreateDependency(Collection<DependencyDefinition> injectedDependencies) {
        return injectedDependencies.isEmpty() || containsAllByName(injectedDependencies);
    }

    private boolean containsAllByName(Collection<DependencyDefinition> injectedDependencies) {
        return injectedDependencies.stream()
                .allMatch(d -> containsDependencyByName(d.getName()));
    }

    private boolean containsDependencyByName(final String name) {
        return dependencyMap.keySet().stream()
                .anyMatch(d -> name.equals(d.getName()));
    }

    private Object createInjectedInstance(DependencyDefinition dependency) {
        try {
            String injectedDependencyMethodName = dependency.getInjectedDependencyMethodName();
            Class<?> configClass = Class.forName(dependency.getConfigClassQualifiedName());
            Class<?>[] argTypes = getDependencyArgTypes(injectedDependencyMethodName, configClass);
            Method declaredMethod = configClass.getDeclaredMethod(injectedDependencyMethodName, argTypes);
            Object[] injectedArgs = getInjectedArgs(dependency);
            Object configInstance = configClass.getConstructor().newInstance();
            return declaredMethod.invoke(configInstance, injectedArgs);
        } catch (Exception e) {
            LOG.error("Can't create object from dependency definition", e);
            throw new InstanceInjectionException();
        }
    }

    private Object[] getInjectedArgs(DependencyDefinition dependency) {
        return dependency.getDependencyDefinitions().stream()
                .map(obj -> tryToGetDependencyByName(obj.getName()))
                .map(d -> dependencyMap.get(d))
                .collect(Collectors.toList())
                .toArray();
    }

    private DependencyDefinition tryToGetDependencyByName(final String name) {
        return dependencyMap.keySet().stream()
                .filter(d -> name.equals(d.getName()))
                .findFirst()
                .get();
    }

    private Class<?>[] getDependencyArgTypes(String injectedDependencyMethodName, Class<?> configClass) {
        return Arrays.stream(configClass.getMethods())
                .filter(m -> injectedDependencyMethodName.contains(m.getName()))
                .findFirst()
                .get()
                .getParameterTypes();
    }
}
