package com.bobocode.petros.injector;

import com.bobocode.petros.container.DependencyDefinition;
import com.bobocode.petros.scaner.DependencyScanner;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class AnnotationDependencyInjectorTest {

    private final static String PACKAGE_NAME = "str";

    private static Map<String, List<DependencyDefinition>> map = new HashMap<>();

    @BeforeAll
    static void setup() {
        DependencyDefinition doorsDefinition = new DependencyDefinition();
        doorsDefinition.setQualifiedName(Doors.class.getName());
        doorsDefinition.setName(Doors.class.getSimpleName());

        DependencyDefinition windowDefinition = new DependencyDefinition();
        windowDefinition.setName(Window.class.getSimpleName());
        windowDefinition.setQualifiedName(Window.class.getName());

        DependencyDefinition wallDefinition = new DependencyDefinition();
        wallDefinition.setQualifiedName(Wall.class.getName());
        wallDefinition.setName(Wall.class.getSimpleName());
        wallDefinition.addInjectedDependencyDefinition(windowDefinition);
        wallDefinition.addInjectedDependencyDefinition(doorsDefinition);

        DependencyDefinition wallDefinition1 = new DependencyDefinition();
        wallDefinition1.setQualifiedName(Wall.class.getName());
        wallDefinition1.setName(Wall.class.getSimpleName());
        wallDefinition1.addInjectedDependencyDefinition(windowDefinition);
        wallDefinition1.addInjectedDependencyDefinition(doorsDefinition);

        DependencyDefinition stringDefinition = new DependencyDefinition();
        stringDefinition.setQualifiedName(String.class.getName());
        stringDefinition.setName(String.class.getSimpleName());

        DependencyDefinition roomDefinition = new DependencyDefinition();
        roomDefinition.setName(Room.class.getSimpleName());
        roomDefinition.setQualifiedName(Room.class.getName());
        roomDefinition.addInjectedDependencyDefinition(wallDefinition);
        roomDefinition.addInjectedDependencyDefinition(stringDefinition);

        map.put(Room.class.getName(), List.of(roomDefinition));
        map.put(Wall.class.getName(), List.of(wallDefinition1, wallDefinition));
        map.put(Window.class.getName(), List.of(windowDefinition));
        map.put(Doors.class.getName(), List.of(doorsDefinition));
        map.put(String.class.getName(), List.of(stringDefinition));
    }

    @Mock
    private DependencyScanner scanner;

    private AnnotationDependencyInjector sut = new AnnotationDependencyInjector(PACKAGE_NAME);

    @Test
    void injectedDependencyDefinitionObjectMap() throws NoSuchFieldException, IllegalAccessException {
        Field classScanner = sut.getClass().getDeclaredField("classScanner");
        classScanner.setAccessible(true);
        classScanner.set(sut, scanner);
        classScanner.setAccessible(false);

        Mockito.when(scanner.scan(Mockito.anyString())).thenReturn(map);
        Map<DependencyDefinition, Object> map = sut.injectedDependencyDefinitionObjectMap();
        System.out.println(map);
    }

    public static class Room {
        private Wall wall;
        private Wall wall1;
        private String string;

        public Room() {
        }

        public Room(Wall wall, Wall wall1, String string) {
            this.wall = wall;
            this.wall1 = wall1;
            this.string = string;
        }
    }

    public static class Wall {
        private Window window;
        private Doors doors;

        public Wall() {
        }

        public Wall(Window window, Doors doors) {
            this.window = window;
            this.doors = doors;
        }
    }

    public static class Window {
        public Window() {
        }
    }

    public static class Doors {
        public Doors() {
        }
    }
}