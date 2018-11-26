package io.github.anycollect.extensions.exceptions;

public final class MissingRequiredPropertyException extends ConfigurationException {
    private final String property;

    public MissingRequiredPropertyException(final String property) {
        super(String.format("property %s is missed", property));
        this.property = property;
    }

    public String getProperty() {
        return property;
    }
}
