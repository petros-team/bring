package com.bobocode.petros.testpackage.scan.dependency;

import com.bobocode.petros.annotation.Dependency;
import com.bobocode.petros.annotation.Injected;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Dependency
@NoArgsConstructor
public class SuccessfullyBuildDefinitionDependencyCase {

    @Injected
    public SuccessfullyBuildDefinitionDependencyCase(String testName, ArrayList<String> listStringTest) {
    }
}
