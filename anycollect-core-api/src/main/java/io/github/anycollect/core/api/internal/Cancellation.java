package io.github.anycollect.core.api.internal;

public interface Cancellation {
    Cancellation NOOP = () -> {
    };

    static Cancellation noop() {
        return NOOP;
    }

    default Cancellation andThen(Cancellation cancellation) {
        return () -> {
            cancel();
            cancellation.cancel();
        };
    }

    void cancel();
}
