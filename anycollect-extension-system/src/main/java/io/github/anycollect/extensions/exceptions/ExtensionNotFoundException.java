package io.github.anycollect.extensions.exceptions;

public final class ExtensionNotFoundException extends ConfigurationException {
    private final String className;

    public ExtensionNotFoundException(final String className) {
        super(String.format("extension %s not found", className));
        this.className = className;
    }

    public String getClassName() {
        return className;
    }
}
