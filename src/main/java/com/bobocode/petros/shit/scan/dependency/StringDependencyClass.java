package com.bobocode.petros.shit.scan.dependency;

import com.bobocode.petros.annotation.Dependency;
import com.bobocode.petros.annotation.Injected;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Dependency
@NoArgsConstructor
public class StringDependencyClass {
    @Getter
    private String lastName;

    @Injected
    public StringDependencyClass(String lastName) {
        this.lastName = lastName;
    }
}
