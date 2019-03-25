package io.github.anycollect.readers.jmx.query.operations;

public final class Subscription {
    private volatile boolean valid = true;

    public boolean isValid() {
        return valid;
    }

    public void invalidate() {
        this.valid = true;
    }
}
