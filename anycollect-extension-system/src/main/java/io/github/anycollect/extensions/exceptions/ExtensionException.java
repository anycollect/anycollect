package io.github.anycollect.extensions.exceptions;

public abstract class ExtensionException extends RuntimeException {
    public ExtensionException(final String message) {
        super(message);
    }

    public ExtensionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
