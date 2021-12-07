package com.bobocode.petros.injector;

import com.bobocode.petros.annotation.Injected;
import com.bobocode.petros.container.DependencyDefinition;
import com.bobocode.petros.exception.DependencyClassNotFoundException;
import com.bobocode.petros.exception.InstanceInjectionException;
import com.bobocode.petros.exception.MultipleInjectConstructorsException;
import com.bobocode.petros.scaner.AnnotationDependencyClassScanner;
import com.bobocode.petros.scaner.AnnotationDependencyConfigurationScanner;
import com.bobocode.petros.scaner.DependencyScanner;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        var scannedConfigurationsMap = configurationScanner.scan(packageName);
        var scannedClassesMap = classScanner.scan(packageName);
        createConfigClassDependencies(scannedConfigurationsMap);
        createConfigClassDependencies(scannedClassesMap);
        return dependencyMap;
    }

    private void createConfigClassDependencies(Map<String, List<DependencyDefinition>> scannedDependencies) {
        getDefinitionsSortedByArgsQuantity(scannedDependencies)
                .stream()
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
            injectConfigDependency(definition, injectedDependencies);
        } else {
            injectClassDependency(definition, injectedDependencies);
        }
    }

    private void injectConfigDependency(DependencyDefinition definition,
                                        Collection<DependencyDefinition> injectedDefinitions) {
        if (isReadyToCreateDependency(injectedDefinitions)) {
            dependencyMap.put(definition, createInjectedConfigInstance(definition));
        } else {
            injectDependency(getAbsentDefinition(injectedDefinitions));
        }
    }

    private void injectClassDependency(DependencyDefinition definition,
                                       Collection<DependencyDefinition> injectedDependencies) {
        if (isReadyToCreateDependency(injectedDependencies)) {
            dependencyMap.put(definition, createInjectedClassInstance(definition));
        } else {
            injectDependency(getAbsentDefinition(injectedDependencies));
        }

    }

    @SneakyThrows
    private Object createInjectedClassInstance(DependencyDefinition definition) {
        Class<?> dependencyClass = getClassForName(definition.getQualifiedName());
        if (hasConstructorMarkedAsInject(dependencyClass)) {
            Constructor<?> injectedConstructor = Arrays.stream(dependencyClass.getDeclaredConstructors())
                    .filter(a -> a.isAnnotationPresent(Injected.class))
                    .findAny()
                    .orElseThrow();
            var construcotrArgs = Arrays.stream(injectedConstructor.getParameters())
                    .map(parameter -> parameter.getName())
                    .map(name -> dependencyMap.get(tryToGetDependencyByName(name)))
                    .toArray();
            return injectedConstructor.newInstance(construcotrArgs);
        } else {
            return dependencyClass.getDeclaredConstructor().newInstance();
        }
    }

    private Class<?> getClassForName(String qualifiedName) {
        try {
            return Class.forName(qualifiedName);
        } catch (ClassNotFoundException e) {
            throw new DependencyClassNotFoundException(e.getMessage(), e);
        }
    }

    private boolean hasConstructorMarkedAsInject(Class<?> aClass) {
        var injectedConstructors = Arrays.stream(aClass.getDeclaredConstructors())
                .filter(a -> a.isAnnotationPresent(Injected.class))
                .collect(Collectors.toList());
        if (injectedConstructors.size() > 1) {
            throw new MultipleInjectConstructorsException(aClass.getName());
        }
        return !injectedConstructors.isEmpty();
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

    private Object createInjectedConfigInstance(DependencyDefinition dependency) {
        try {
            String injectedDependencyMethodName = dependency.getConfigDependencyMethodName();
            Class<?> configClass = Class.forName(dependency.getConfigClassQualifiedName());
            Class<?>[] argTypes = getDependencyArgTypes(injectedDependencyMethodName, configClass);
            Method declaredMethod = configClass.getDeclaredMethod(injectedDependencyMethodName, argTypes);
            Object[] injectedArgs = getInjectedArgs(dependency);
            Object configInstance = configClass.getConstructor().newInstance();
            return declaredMethod.invoke(configInstance, injectedArgs);
        } catch (IllegalAccessException | InvocationTargetException | ClassNotFoundException
                | NoSuchMethodException | InstantiationException e) {
            throw new InstanceInjectionException(e.getMessage(), e);
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
