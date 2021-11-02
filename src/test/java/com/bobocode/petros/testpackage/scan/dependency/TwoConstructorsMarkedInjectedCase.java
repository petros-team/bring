package com.bobocode.petros.testpackage.scan.dependency;

import com.bobocode.petros.annotation.Dependency;
import com.bobocode.petros.annotation.Injected;
import lombok.NoArgsConstructor;

@Dependency
@NoArgsConstructor
public class TwoConstructorsMarkedInjectedCase {

    @Injected
    public TwoConstructorsMarkedInjectedCase(String name) {
    }

    @Injected
    public TwoConstructorsMarkedInjectedCase(String testString, Number testBoolean) {
    }
}
