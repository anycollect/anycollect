package io.github.anycollect.extensions.exceptions;

public final class WrongExtensionClassException extends ConfigurationException {
    private final Class<?> extensionPointClass;
    private final Class<?> extensionClass;

    public WrongExtensionClassException(final Class<?> extensionPointClass, final Class<?> extensionClass) {
        super("");
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
