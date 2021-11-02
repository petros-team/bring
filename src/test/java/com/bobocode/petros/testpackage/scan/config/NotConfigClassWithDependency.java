package com.bobocode.petros.testpackage.scan.config;

import com.bobocode.petros.annotation.Dependency;

import java.util.concurrent.ThreadLocalRandom;

/*
In case if this class will be scanned the test will fail, since map will contain two Integer DependencyDefinition
*/
public class NotConfigClassWithDependency {
    @Dependency
    public Integer notConfigClassDependency() {
        return ThreadLocalRandom.current().nextInt();
    }
}
