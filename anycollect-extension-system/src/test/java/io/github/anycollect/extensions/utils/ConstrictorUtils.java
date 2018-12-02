package io.github.anycollect.extensions.utils;

import java.lang.reflect.Constructor;
import java.util.Arrays;

public class ConstrictorUtils {
    public static <T> Constructor<T> createFor(Class<T> type, Class<?>... args) {
        try {
            return type.getDeclaredConstructor(args);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(type + " doesn't contain appropriate constructor with parameters: "
                    + Arrays.toString(args), e);
        }
    }
}
