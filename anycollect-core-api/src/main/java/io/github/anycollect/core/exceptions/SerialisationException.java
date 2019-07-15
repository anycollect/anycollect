package io.github.anycollect.core.exceptions;

public class SerialisationException extends Exception {
    public SerialisationException(final String message) {
        super(message);
    }

    public SerialisationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
