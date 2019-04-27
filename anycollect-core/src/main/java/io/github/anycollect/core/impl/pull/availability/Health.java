package io.github.anycollect.core.impl.pull.availability;

public enum Health {
    PASSED(0), UNKNOWN(1), FAILED(2);
    private final int statusCode;

    Health(final int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
