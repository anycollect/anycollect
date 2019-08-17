package io.github.anycollect.extensions.exceptions;

import io.github.anycollect.core.exceptions.ConfigurationException;

import java.util.Set;

public final class WrongDependencyClassException extends ConfigurationException {
    public WrongDependencyClassException(final String name,
                                         final Class<?> expected,
                                         final Set<Class<?>> actual) {
        super(String.format("dependency %s requires parameter of type %s, but given %s", name, expected, actual));
    }
}
