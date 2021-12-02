package com.bobocode.petros.container;

import com.bobocode.petros.exception.NoUniqueDependecyException;
import com.bobocode.petros.injector.DependencyInjector;

/**
 * The main purpose of {@link ApplicationContainer} is keeping the instances of dependencies, which are used in user's application.
 *  It creates {@link DependencyInjector} instance, passing the package name of the application. {@link DependencyInjector} returns the Map<{@link DependencyDefinition}, Object>
 *  where all created dependencies are stored. Using the methods of this class, you can retrieve the exact instance of dependency by Name or by Type.
 *  For proper use of the Bring you should use annotations {@link com.bobocode.petros.annotation.Dependency}, {@link com.bobocode.petros.annotation.ConfigClass} and
 *  {@link com.bobocode.petros.annotation.Injected} in your Java application according to the readme file.
 *
 * @author <i>Serhii Feshchuk</i>
 */

public interface ApplicationContainer {
    /**
     * Method return dependency instance from <b>dependencyMap</b> by name and type
     * @param name <b>String</b>, name of the dependency
     * @param clazz <b>Class</b>, type of the dependency
     * @return dependency instance
     */
    <T> T getDependency(String name, Class<T> clazz);
    /**
     * Method return dependency instance from <b>dependencyMap</b> by name and type. It throws {@link NoUniqueDependecyException} if there is more,
     * than one dependency of the specified type
     * @param clazz <b>Class</b>, type of the dependency
     * @return dependency instance
     */
    <T> T getDependency(Class<T> clazz);
}
