package io.github.anycollect.core.api.internal;

public interface Clock {
    Clock DEFAULT = System::currentTimeMillis;

    long time();

    static Clock getDefault() {
        return DEFAULT;
    }
}
