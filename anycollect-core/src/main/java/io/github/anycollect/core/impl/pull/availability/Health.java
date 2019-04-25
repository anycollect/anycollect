package io.github.anycollect.core.impl.pull.availability;

public enum Health {
    PASSED(0), FAILED(1), UNKNOWN(2);
    private final int value;

    Health(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
