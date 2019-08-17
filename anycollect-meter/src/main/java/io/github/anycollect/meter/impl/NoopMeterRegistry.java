package io.github.anycollect.meter.impl;

import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.meter.api.*;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

@Extension(name = NoopMeterRegistry.NAME, contracts = MeterRegistry.class)
public final class NoopMeterRegistry extends MeterRegistry {
    public static final String NAME = "NoopMeterRegistry";

    @Nonnull
    @Override
    protected <T> Gauge gauge(@Nonnull final MeterId id, @Nonnull final T obj, @Nonnull final ToDoubleFunction<T> value) {
        return Gauge.NOOP;
    }

    @Nonnull
    @Override
    protected Counter counter(@Nonnull final MeterId id) {
        return Counter.NOOP;
    }

    @Nonnull
    @Override
    protected <T> FunctionCounter counter(@Nonnull final MeterId id, @Nonnull final T obj, @Nonnull final ToLongFunction<T> value) {
        return FunctionCounter.NOOP;
    }

    @Nonnull
    @Override
    protected Distribution distribution(@Nonnull final MeterId id) {
        return Distribution.NOOP;
    }

    @Nonnull
    @Override
    protected Timer timer(@Nonnull final MeterId id, @Nonnull final TimeUnit timeUnit) {
        return Timer.NOOP;
    }
}
