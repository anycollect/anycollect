package io.github.anycollect.extensions.exceptions;

import io.github.anycollect.core.exceptions.ConfigurationException;

/**
 * Signals that configuration does not contain a required property.
 */
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
