package io.github.anycollect.extensions.exceptions;

public final class WrongDependencyClassException extends ConfigurationException {
    public WrongDependencyClassException(final String name,
                                         final Class<?> expected,
                                         final Class<?> actual) {
        super(String.format("dependency %s requires parameter of type %s, but given %s", name, expected, actual));
    }
}
