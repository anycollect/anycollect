package io.github.anycollect.extensions.exceptions;

/**
 * Signals that extension class and extension point class specified in configuration are incompatible.
 */
public final class WrongExtensionMappingException extends ConfigurationException {
    private final Class<?> extensionPointClass;
    private final Class<?> extensionClass;

    public WrongExtensionMappingException(final Class<?> extensionPointClass, final Class<?> extensionClass) {
        super(String.format("%s is wrong implementation of %s", extensionClass, extensionPointClass));
        this.extensionPointClass = extensionPointClass;
        this.extensionClass = extensionClass;
    }

    public Class<?> getExtensionPointClass() {
        return extensionPointClass;
    }

    public Class<?> getExtensionClass() {
        return extensionClass;
    }
}
