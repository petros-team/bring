package com.bobocode.petros;

import com.bobocode.petros.container.ApplicationAnnotationContainer;
import com.bobocode.petros.shit.scan.dependency.TwoClassesDependency;

public class TestApplication {
    public static void main(String[] args) {
        var applicationAnnotationContainer = new ApplicationAnnotationContainer("com.bobocode.petros");
        var dependency = applicationAnnotationContainer.getDependency(TwoClassesDependency.class);
        dependency.printFullMessage();
    }
}
