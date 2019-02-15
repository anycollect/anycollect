package io.github.anycollect.core.exceptions;

public class ConnectionException extends Exception {
    public ConnectionException(final String message) {
        super(message);
    }

    public ConnectionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
