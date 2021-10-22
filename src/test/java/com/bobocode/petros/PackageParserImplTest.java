package com.bobocode.petros;

import com.bobocode.petros.exception.DefaultNoArgsConstructor;
import com.bobocode.petros.exception.NotPublicConstructorModifiers;
import com.bobocode.petros.interfaces.PackageParser;
import com.bobocode.petros.resource.case_three.FirstComponentTest;
import com.bobocode.petros.resource.case_three.case_three_one.FifthComponentTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PackageParserImplTest {

    private PackageParserImplTest() {
    }

    private PackageParser packageParser;

    @BeforeEach
    public void setUp() {
        packageParser = new PackageParserImpl();
    }

    @Test
    @SneakyThrows
    public void shouldThrowExceptionWhenParameterIsEmpty() {
        assertThrowsExactly(ClassNotFoundException.class,
                () -> packageParser.parse(""));
    }

    @Test
    @SneakyThrows
    public void shouldThrowExceptionWhenParameterNull() {
        assertThrowsExactly(ClassNotFoundException.class,
                () -> packageParser.parse(null));
    }

    @Test
    @SneakyThrows
    public void shouldBeAvailableBringComponentInDownPackage(){
        final Map<String, Object> parser = packageParser.parse("com.bobocode.petros.resource.case_three");
        assertEquals(FifthComponentTest.class, parser.get("fifthComponentTest").getClass());
    }

    @Test
    @SneakyThrows
    public void shouldBeAvailableBringComponentInMap() {
        final Map<String, Object> parser = packageParser.parse("com.bobocode.petros.resource.case_three");
        assertEquals(FirstComponentTest.class, parser.get("firstComponentTest").getClass());
    }

    @Test
    @SneakyThrows
    public void shouldThrowExceptionWhenBringComponentNotHaveDefaultConstructorOrHaveMoreOneParameter() {
        assertThrowsExactly(DefaultNoArgsConstructor.class,
                () -> packageParser.parse("com.bobocode.petros.resource.case_two"));
    }

    @Test
    @SneakyThrows
    public void shouldThrowExceptionWhenDefaultConstructorNotPublic() {
        assertThrowsExactly(NotPublicConstructorModifiers.class,
                () -> packageParser.parse("com.bobocode.petros.resource.case_one"));
    }

}

