package io.github.anycollect.extensions.di;

import io.github.anycollect.extensions.definitions.Dependency;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;

@ToString
@EqualsAndHashCode
public final class InstantiatorImpl implements Instantiator {
    private final Constructor<?> constructor;

    public InstantiatorImpl(final Constructor<?> constructor) {
        Objects.requireNonNull(constructor, "constructor must not be null");
        this.constructor = constructor;
    }

    @Override
    public Object instantiate(final List<Dependency> dependencies)
            throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Object[] args = new Object[constructor.getParameterCount()];
        for (Dependency dependency : dependencies) {
            InjectionPoint injectionPoint = dependency.inject();
            args[injectionPoint.getPosition()] = injectionPoint.getDependency();
        }
        return constructor.newInstance(args);
    }
}
