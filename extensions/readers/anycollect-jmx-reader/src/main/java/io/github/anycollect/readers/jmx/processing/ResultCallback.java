package io.github.anycollect.readers.jmx.processing;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface ResultCallback {
    ResultCallback NOOP = result -> {
    };

    void call(@Nonnull QueryResult result);

    static ResultCallback noop() {
        return NOOP;
    }
}
