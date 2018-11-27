package io.github.anycollect.extensions.exceptions;

import java.lang.reflect.Constructor;

public class UnresolvableConstructorException extends ExtensionDescriptorException {
    public UnresolvableConstructorException(final Class<?> extensionClass,
                                            final Constructor<?> constructor) {
        super(String.format("extension %s has unresolvable constructor %s", extensionClass, constructor));
    }
}
