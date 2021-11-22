package com.bobocode.petros.shit.scan.dependency;

import com.bobocode.petros.annotation.Dependency;

@Dependency
public class SimpleClass {
    public String getMessage(){
        return "Hello from SimpleClass";
    }
}
