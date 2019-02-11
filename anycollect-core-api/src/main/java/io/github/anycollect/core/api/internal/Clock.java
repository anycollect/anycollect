package io.github.anycollect.core.api.internal;

public interface Clock {
    Clock DEFAULT = System::nanoTime;

    long monotonicTime();

    static Clock getDefault() {
        return DEFAULT;
    }
}
