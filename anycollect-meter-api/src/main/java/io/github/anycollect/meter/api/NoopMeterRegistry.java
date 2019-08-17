package io.github.anycollect.meter.api;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

final class NoopMeterRegistry extends MeterRegistry {
    static final NoopMeterRegistry INSTANCE = new NoopMeterRegistry();

    private NoopMeterRegistry() {
    }

    @Nonnull
    @Override
    public <T> Gauge gauge(@Nonnull final MeterId id,
                           @Nonnull final T obj,
                           @Nonnull final ToDoubleFunction<T> value) {
        return Gauge.NOOP;
    }

    @Nonnull
    @Override
    public Counter counter(@Nonnull final MeterId id) {
        return Counter.NOOP;
    }

    @Nonnull
    @Override
    public <T> FunctionCounter counter(@Nonnull final MeterId id,
                                       @Nonnull final T obj,
                                       @Nonnull final ToLongFunction<T> value) {
        return FunctionCounter.NOOP;
    }

    @Nonnull
    @Override
    public Distribution distribution(@Nonnull final MeterId id) {
        return Distribution.NOOP;
    }

    @Nonnull
    @Override
    public Timer timer(@Nonnull final MeterId id, @Nonnull final TimeUnit timeUnit) {
        return Timer.NOOP;
    }
}
