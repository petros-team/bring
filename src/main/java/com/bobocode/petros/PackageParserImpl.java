package com.bobocode.petros;

import com.bobocode.petros.annotation.BringComponent;
import com.bobocode.petros.exception.DefaultNoArgsConstructor;
import com.bobocode.petros.exception.NotPublicConstructorModifiers;
import com.bobocode.petros.interfaces.PackageParser;
import lombok.SneakyThrows;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The PackageParserImp search in packages our
 * classes that mark as {@link BringComponent}.
 * <p>
 * Then collecting this class in {@link Map} where
 * <b>key</b> is {@link String} of name our
 * classes (only first letter in lowercase) and
 * our <b>value</b> it is {@link Object} which hold
 * our class that mark as {@link BringComponent}.
 *
 * @author Makiyan
 * @version 0.1 alpha-beta
 * @see PackageParser
 */

public class PackageParserImpl implements PackageParser {

    //key   - name of class (only first letter in lowercase)
    //value - our class that mark as @BringComponent
    private Map<String, Object> components
            = new ConcurrentHashMap<>();

    //Utils
    private static String MESSAGE_CONSTR = "doesn't have constructors or have one more parameter";
    private static String MESSAGE_CLASS = "Please write correct package name";
    private static int CONSTRUCTOR_PARAM = 0;

    /**
     * Parser perform main operation parsing
     * of our package and collect to {@link Map}.
     *
     * @param packageName path to package which you wanna be collected.
     * @return {@link Map} where key is name of class
     * <b>(only first letter in lowercase)</b> , key is our class.
     * @throws ClassNotFoundException   package name must not be empty or null.
     * @throws DefaultNoArgsConstructor if class doesn't have default constructor.
     * @since 0.1 alpha-beta
     */

    @Override
    @SneakyThrows
    public Map<String, Object> parse(String packageName) {
        if (packageName == null || packageName.isEmpty()) {
            throw new ClassNotFoundException(MESSAGE_CLASS);
        }
        //com.bobocode
        var thread = Thread.currentThread();
        var context = thread.getContextClassLoader();

        var path = packageName.replace('.', '/');

        Enumeration<URL> resources = context.getResources(path);

        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }

        for (File directory : dirs) {
            findClasses(directory);
        }
        return components;
    }

    @SneakyThrows
    private void findClasses(File directory) {
        File[] dirs = directory.listFiles();
        Objects.requireNonNull(dirs);

        for (File file : dirs) {
            if (file.isDirectory()) {
                findClasses(file);
            } else if (file.getName().endsWith(".class")) {

                Class<?> ourClass = Class.forName(getSimpleClassPath(file));

                if (checkIfPresentAnnotationBringComponent(ourClass) &&
                        checkIfConstructorHasPublicModifiers(ourClass)) {

                    components.putIfAbsent(getSimpleName(file),
                            ourClass.getDeclaredConstructor().newInstance());
                }
            }
        }
    }

    private String getSimpleName(File file) {
        String rmClassPrefix = file.getName().substring(0, file.getName().length() - 6);
        return getNameOfClass(rmClassPrefix);
    }

    private static String getNameOfClass(String nameOfClass) {
        var srt = nameOfClass.substring(0, 1).toLowerCase(Locale.ROOT);
        return srt.concat(nameOfClass.substring(1));
    }

    private static String getSimpleClassPath(File file) {
        String absolutePath = file.getPath().replace("/", ".");
        //TODO
        return absolutePath.substring(absolutePath.indexOf("com")).replace(".class", "");
    }


    private boolean checkIfPresentAnnotationBringComponent(Class<?> aClass) {
        return aClass.isAnnotationPresent(BringComponent.class);
    }

    private boolean checkIfNoArgsConstructorPresent(Class<?> aClass) {
        Arrays.stream(aClass.getDeclaredConstructors())
                .filter(a -> a.getParameterCount() == CONSTRUCTOR_PARAM)
                .findAny()
                .orElseThrow(() -> new DefaultNoArgsConstructor(aClass.getName() + MESSAGE_CONSTR));
        return true;
    }

    private boolean checkIfConstructorHasPublicModifiers(Class<?> aClass) {
        Arrays.stream(aClass.getDeclaredConstructors())
                .filter(a -> a.getModifiers() == Modifier.PUBLIC)
                .findAny()
                .orElseThrow(() -> new NotPublicConstructorModifiers(aClass.getName() + " of constructor closed"));

        return checkIfNoArgsConstructorPresent(aClass);
    }
}
