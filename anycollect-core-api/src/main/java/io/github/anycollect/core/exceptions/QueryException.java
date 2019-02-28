package io.github.anycollect.core.exceptions;

public class QueryException extends Exception {
    public QueryException(final String message) {
        super(message);
    }

    public QueryException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
