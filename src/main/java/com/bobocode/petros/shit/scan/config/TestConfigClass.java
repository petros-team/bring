package com.bobocode.petros.shit.scan.config;

import com.bobocode.petros.annotation.ConfigClass;
import com.bobocode.petros.annotation.Dependency;

@ConfigClass
public class TestConfigClass {
    @Dependency
    public Integer luckyNumber() {
        return 777;
    }

    @Dependency(name = "namedDependency")
    public NamedDependency namedDependencyMethod(Integer luckyNumber) {
        return new NamedDependency(luckyNumber);
    }

    public record NamedDependency(Integer number) {
    }
}
