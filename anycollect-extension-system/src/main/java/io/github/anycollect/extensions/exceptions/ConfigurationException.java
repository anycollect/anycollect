package io.github.anycollect.extensions.exceptions;

public class ConfigurationException extends RuntimeException {
    ConfigurationException(final String message) {
        super(message);
    }

    public ConfigurationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
