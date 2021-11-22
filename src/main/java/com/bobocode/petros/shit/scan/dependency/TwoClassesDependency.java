package com.bobocode.petros.shit.scan.dependency;

import com.bobocode.petros.annotation.Dependency;
import com.bobocode.petros.annotation.Injected;
import lombok.NoArgsConstructor;

@Dependency
@NoArgsConstructor
public class TwoClassesDependency {
    private StringDependencyClass stringDependencyClass;
    private SimpleClass simpleClass;

    @Injected
    public TwoClassesDependency(StringDependencyClass stringDependencyClass, SimpleClass simpleClass) {
        this.stringDependencyClass = stringDependencyClass;
        this.simpleClass = simpleClass;
    }

    public void printFullMessage(){
        System.out.println("StringDependencyClass - " + stringDependencyClass.getLastName());;
        System.out.println("TwoClassesDependency - " + simpleClass.getMessage());
    }
}
