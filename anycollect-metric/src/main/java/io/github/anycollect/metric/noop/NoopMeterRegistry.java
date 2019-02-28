package io.github.anycollect.metric.noop;

import io.github.anycollect.metric.*;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

public final class NoopMeterRegistry implements MeterRegistry {
    @Nonnull
    @Override
    public <T> Gauge gauge(@Nonnull final MeterId id,
                           @Nonnull final T obj,
                           @Nonnull final ToDoubleFunction<T> value) {
        return new NoopGauge(id);
    }

    @Nonnull
    @Override
    public Counter counter(@Nonnull final MeterId id) {
        return new NoopCounter(id);
    }

    @Nonnull
    @Override
    public <T> FunctionCounter counter(@Nonnull final MeterId id,
                                       @Nonnull final T obj,
                                       @Nonnull final ToDoubleFunction<T> value) {
        return new NoopFunctionCounter(id);
    }

    @Nonnull
    @Override
    public Distribution distribution(@Nonnull final MeterId id) {
        return new NoopDistribution(id);
    }

    @Override
    public List<MetricFamily> measure(@Nonnull final Predicate<MeterId> filter) {
        return Collections.emptyList();
    }
}
