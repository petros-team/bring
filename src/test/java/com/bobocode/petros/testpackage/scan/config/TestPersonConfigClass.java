package com.bobocode.petros.testpackage.scan.config;

import com.bobocode.petros.annotation.ConfigClass;
import com.bobocode.petros.annotation.Dependency;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@ConfigClass
public class TestPersonConfigClass {
    public static final String NAME = "PersonName";
    public static final String LAST_NAME = "PersonLastName";

    @Dependency
    public String name() {
        return NAME;
    }

    @Dependency
    public String lastName() {
        return LAST_NAME;
    }

    @Dependency
    public Person person(String name, String lastName) {
        return new Person(name, lastName);
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Person {
        private String name;
        private String lastName;

        public String name() {
            return name;
        }

        public String lastName() {
            return lastName;
        }

    }
}
