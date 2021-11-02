package com.bobocode.petros.testpackage.scan.dependency;

import com.bobocode.petros.annotation.Dependency;
import com.bobocode.petros.annotation.Injected;

@Dependency
public class DontHaveDefaultConstructorCase {

    @Injected
    public DontHaveDefaultConstructorCase(String testString) {
    }
}
