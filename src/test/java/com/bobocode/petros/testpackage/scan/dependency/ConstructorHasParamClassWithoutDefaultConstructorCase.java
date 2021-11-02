package com.bobocode.petros.testpackage.scan.dependency;

import com.bobocode.petros.annotation.Dependency;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Dependency
@NoArgsConstructor
public class ConstructorHasParamClassWithoutDefaultConstructorCase {

    public ConstructorHasParamClassWithoutDefaultConstructorCase(String testString, TestClass testClass) {
    }

    @AllArgsConstructor
    static class TestClass{
        String testString;
    }
}
