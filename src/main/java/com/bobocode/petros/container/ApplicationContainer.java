package com.bobocode.petros.container;

public interface ApplicationContainer {
    <T> T getDependency(String name, Class<T> clazz);

    <T> T getDependency(Class<T> clazz);
}
