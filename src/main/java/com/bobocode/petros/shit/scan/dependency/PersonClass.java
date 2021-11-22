package com.bobocode.petros.shit.scan.dependency;

import com.bobocode.petros.annotation.Dependency;
import com.bobocode.petros.annotation.Injected;
import com.bobocode.petros.shit.scan.config.TestPersonConfigClass;
import lombok.NoArgsConstructor;

@Dependency
@NoArgsConstructor
public class PersonClass {

    @Injected
    public PersonClass(TestPersonConfigClass.Person person) {
        System.out.println("====================================================");
        System.out.println(person.name());
        System.out.println(person.lastName());
        System.out.println("====================================================");
    }
}
