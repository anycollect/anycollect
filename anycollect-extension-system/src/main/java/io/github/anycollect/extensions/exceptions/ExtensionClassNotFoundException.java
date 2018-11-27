package io.github.anycollect.extensions.exceptions;

/**
 * Signals that extension class or extension point class is not found in classpath.
 */
public final class ExtensionClassNotFoundException extends ConfigurationException {
    private final String className;

    public ExtensionClassNotFoundException(final String className) {
        super(String.format("extension %s not found", className));
        this.className = className;
    }

    public String getClassName() {
        return className;
    }
}
