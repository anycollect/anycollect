package io.github.anycollect.core.impl.scheduler;

public interface Cancellation {
    Cancellation NOOP = () -> {
    };

    static Cancellation noop() {
        return NOOP;
    }

    void cancel();
}
