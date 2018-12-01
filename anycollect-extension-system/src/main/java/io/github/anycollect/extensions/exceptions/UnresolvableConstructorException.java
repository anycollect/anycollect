package io.github.anycollect.extensions.exceptions;

import java.lang.reflect.Constructor;
import java.util.Set;

public class UnresolvableConstructorException extends ExtensionDescriptorException {
    public UnresolvableConstructorException(final Class<?> extensionClass,
                                            final Constructor<?> constructor,
                                            final Class<?> unresolvableParameter) {
        super(String.format("extension %s has unresolvable parameter %s in constructor %s", extensionClass,
                unresolvableParameter,
                constructor));
    }

    public UnresolvableConstructorException(final Class<?> extensionClass,
                                            final Constructor<?> constructor,
                                            final Set<Integer> unresolvableParameters) {
        super(String.format("extension %s has unresolvable parameters %s in constructor %s", extensionClass,
                unresolvableParameters,
                constructor));
    }
}
