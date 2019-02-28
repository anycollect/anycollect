package io.github.anycollect.meter.registry;

import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.metric.*;
import io.github.anycollect.metric.noop.NoopDistribution;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

import static java.util.stream.Collectors.toList;

@Extension(name = AnyCollectMeterRegistry.NAME, point = MeterRegistry.class)
public class AnyCollectMeterRegistry implements MeterRegistry {
    public static final String NAME = "MeterRegistry";
    private final ConcurrentMap<MeterId, Meter> meters = new ConcurrentHashMap<>();
    private final Clock clock = Clock.getDefault();

    @ExtCreator
    public AnyCollectMeterRegistry() {
    }

    @Nonnull
    @Override
    public <T> Gauge gauge(@Nonnull final MeterId id, @Nonnull final T obj, @Nonnull final ToDoubleFunction<T> value) {
        return (Gauge) meters.computeIfAbsent(id, meterId -> new DefaultGauge<>(meterId, obj, value, clock));
    }

    @Nonnull
    @Override
    public Counter counter(@Nonnull final MeterId id) {
        return (Counter) meters.computeIfAbsent(id, meterId -> new CumulativeCounter(meterId, clock));
    }

    @Nonnull
    @Override
    public <T> FunctionCounter counter(@Nonnull final MeterId id,
                                       @Nonnull final T obj,
                                       @Nonnull final ToDoubleFunction<T> value) {
        return (FunctionCounter) meters.computeIfAbsent(id, meterId ->
                new DefaultFunctionCounter<>(id, clock, obj, value));
    }

    @Nonnull
    @Override
    public Distribution distribution(@Nonnull final MeterId id) {
        return new NoopDistribution(id);
    }

    @Override
    public List<MetricFamily> measure(@Nonnull final Predicate<MeterId> filter) {
        return meters.values().stream().filter(meter -> filter.test(meter.getId()))
                .map(Meter::measure)
                .collect(toList());
    }
}
