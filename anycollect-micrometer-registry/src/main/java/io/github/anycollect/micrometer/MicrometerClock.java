package io.github.anycollect.micrometer;


import io.github.anycollect.core.api.internal.Clock;

import javax.annotation.Nonnull;

public final class MicrometerClock implements io.micrometer.core.instrument.Clock {
    private final Clock clock;

    public MicrometerClock(@Nonnull final Clock clock) {
        this.clock = clock;
    }

    @Override
    public long wallTime() {
        return System.currentTimeMillis();
    }

    @Override
    public long monotonicTime() {
        return clock.monotonicTime();
    }
}
