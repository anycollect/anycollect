package io.github.anycollect.readers.jmx;

public class ConnectionException extends Exception {
    public ConnectionException(final String message) {
        super(message);
    }

    public ConnectionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
