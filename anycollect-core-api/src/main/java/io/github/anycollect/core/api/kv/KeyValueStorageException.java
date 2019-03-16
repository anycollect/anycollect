package io.github.anycollect.core.api.kv;

public class KeyValueStorageException extends Exception {
    public KeyValueStorageException(final String message) {
        super(message);
    }

    public KeyValueStorageException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
