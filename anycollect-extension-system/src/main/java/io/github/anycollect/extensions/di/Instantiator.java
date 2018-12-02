package io.github.anycollect.extensions.di;

import io.github.anycollect.extensions.definitions.Dependency;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface Instantiator {
    Object instantiate(List<Dependency> dependencies)
            throws IllegalAccessException, InvocationTargetException, InstantiationException;

    static Instantiator forConstructor(Constructor<?> constructor) {
        return new InstantiatorImpl(constructor);
    }
}
