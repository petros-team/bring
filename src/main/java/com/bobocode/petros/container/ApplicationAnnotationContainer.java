package com.bobocode.petros.container;

import com.bobocode.petros.injector.AnnotationDependencyInjector;
import com.bobocode.petros.injector.DependencyInjector;

@SuppressWarnings("all")
public class ApplicationAnnotationContainer implements ApplicationContainer {
    private DependencyInjector dependencyInjector;

    public ApplicationAnnotationContainer(String packageName) {
        dependencyInjector = new AnnotationDependencyInjector(packageName);
    }

    @Override
    public <T> T getDependency(String name, Class<T> clazz) {
        return null;
    }

    @Override
    public <T> T getDependency(Class<T> clazz) {
        return null;
    }
}
