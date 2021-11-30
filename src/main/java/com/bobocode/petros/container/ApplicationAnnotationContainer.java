package com.bobocode.petros.container;

import com.bobocode.petros.exception.NoUniqueDependecyException;
import com.bobocode.petros.injector.AnnotationDependencyInjector;
import com.bobocode.petros.injector.DependencyInjector;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * The main purpose of {@link ApplicationContainer} is keeping the instances of dependencies, which are used in user's application.
 *  It creates {@link DependencyInjector} instance, passing the package name of the application. {@link DependencyInjector} returns the Map<{@link DependencyDefinition}, Object>
 *  where all created dependencies are stored. Using the methods of this class, you can retrieve the exact instance of dependency by Name or by Type.
 *  For proper use of the Bring you should use annotations {@link com.bobocode.petros.annotation.Dependency}, {@link com.bobocode.petros.annotation.ConfigClass} and
 *  {@link com.bobocode.petros.annotation.Injected} in your Java application according to the readme file.
 *
 * @author <i>Serhii Feshchuk</i>
 */

@Slf4j
public class ApplicationAnnotationContainer implements ApplicationContainer {
    private DependencyInjector dependencyInjector;
    /**
     * main repository of dependencies instances
     */
    private Map<DependencyDefinition, Object> dependencyMap;

    private ApplicationAnnotationContainer() {
    }

    /**
     * Constructor of {@link ApplicationContainer} receives a proper package name and passing it to {@link DependencyInjector}
     * @param packageName String, package name of user's application
     */
    public ApplicationAnnotationContainer(String packageName) {
        LOG.info("Creating instance of dependencyInjector and passing the package name {} for scanning", packageName);
        dependencyInjector = new AnnotationDependencyInjector(packageName);
        dependencyMap = dependencyInjector.injectedDependencyDefinitionObjectMap();
        LOG.info("Successfully received map of dependencies from dependencyInjector");
    }

    /**
     * Method return dependency instance from <b>dependencyMap</b> by name and type
     * @param name <b>String</b>, name of the dependency
     * @param clazz <b>Class</b>, type of the dependency
     * @return dependency instance
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getDependency(String name, Class<T> clazz) {
        LOG.debug("Searching dependency by name {} and by type {}", name, clazz);
        DependencyDefinition definition = keyByDependencyDefinitionName(name);
        LOG.debug("Dependency definition found {}", definition);
        return (T) dependencyMap.get(definition);
    }

    /**
     * Method return dependency instance from <b>dependencyMap</b> by name and type. It throws {@link NoUniqueDependecyException} if there is more,
     * than one dependency of the specified type
     * @param clazz <b>Class</b>, type of the dependency
     * @return dependency instance
     */
    @Override
    public <T> T getDependency(Class<T> clazz) {
        if (isNonUniqueDependency(clazz)){
            LOG.debug("The dependency with such {} type already exists in container",clazz);
            throw new NoUniqueDependecyException(clazz.getName());
        } else {
            return getDependencyFromMap(clazz);
        }
    }

    private DependencyDefinition keyByDependencyDefinitionName(String name){
        return dependencyMap.keySet().stream()
                .filter(dependencyDefinition -> dependencyDefinition.getName().equals(name))
                .findFirst()
                .orElseThrow();
    }

    private <T> boolean isNonUniqueDependency(Class<T> clazz){
        return dependencyMap.values().stream()
                .filter(obj ->  obj.getClass().equals(clazz))
                .count() > 1;
    }

    @SuppressWarnings("unchecked")
    private <T> T getDependencyFromMap(Class<T> clazz){
        return (T) dependencyMap.values().stream()
                .filter(obj ->  obj.getClass().equals(clazz))
                .findFirst()
                .orElseThrow();
    }
}