package io.github.anycollect.extensions.exceptions;

/**
 * Signals that configuration that has been passed is wrong.
 * There is no way to recover from this exception. Because of that this exception is unchecked.
 * The user should fix his configuration.
 */
public class ConfigurationException extends RuntimeException {
    public ConfigurationException(final String message) {
        super(message);
    }

    public ConfigurationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
