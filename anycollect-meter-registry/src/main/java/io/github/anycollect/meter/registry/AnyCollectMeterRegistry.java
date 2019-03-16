package io.github.anycollect.meter.registry;

import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.metric.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

import static java.util.stream.Collectors.toList;

@Extension(name = AnyCollectMeterRegistry.NAME, point = MeterRegistry.class)
public class AnyCollectMeterRegistry implements MeterRegistry {
    public static final String NAME = "MeterRegistry";
    private static final double[] QUANTILES = new double[]{0.5, 0.75, 0.9, 0.95, 0.99, 0.999};
    private final ConcurrentMap<MeterId, Meter> meters = new ConcurrentHashMap<>();
    private final Clock clock = Clock.getDefault();
    private final AnyCollectMeterRegistryConfig config;

    @ExtCreator
    public AnyCollectMeterRegistry(@ExtConfig(optional = true) @Nullable final AnyCollectMeterRegistryConfig config) {
        this.config = config != null ? config : AnyCollectMeterRegistryConfig.DEFAULT;
    }

    @Nonnull
    @Override
    public <T> Gauge gauge(@Nonnull final MeterId id, @Nonnull final T obj, @Nonnull final ToDoubleFunction<T> value) {
        return (Gauge) meters.computeIfAbsent(id, meterId -> DefaultGauge.<T>builder()
                .id(meterId)
                .obj(obj)
                .value(value)
                .prefix(config.globalPrefix())
                .tags(config.commonTags())
                .meta(config.commonMeta())
                .clock(clock)
                .build());
    }

    @Nonnull
    @Override
    public Counter counter(@Nonnull final MeterId id) {
        return (Counter) meters.computeIfAbsent(id, meterId -> CumulativeCounter.builder()
                .id(meterId)
                .prefix(config.globalPrefix())
                .tags(config.commonTags())
                .meta(config.commonMeta())
                .clock(clock)
                .build());
    }

    @Nonnull
    @Override
    public <T> FunctionCounter counter(@Nonnull final MeterId id,
                                       @Nonnull final T obj,
                                       @Nonnull final ToDoubleFunction<T> value) {
        return (FunctionCounter) meters.computeIfAbsent(id, meterId -> DefaultFunctionCounter.<T>builder()
                .id(meterId)
                .obj(obj)
                .value(value)
                .prefix(config.globalPrefix())
                .tags(config.commonTags())
                .meta(config.commonMeta())
                .clock(clock)
                .build());
    }

    @Nonnull
    @Override
    public Distribution distribution(@Nonnull final MeterId id) {
        // TODO configure window and percentiles for each id
        return (Distribution) meters.computeIfAbsent(id, this::makeDistribuition);
    }

    private DistributionMeter makeDistribuition(final MeterId id) {
        // TODO configure window and percentiles for each id
        return CodahaleSlidingTimeWindowDistributionSummary.builder()
                .id(id)
                .quantiles(QUANTILES)
                .window(100)
                .prefix(config.globalPrefix())
                .tags(config.commonTags())
                .meta(config.commonMeta())
                .clock(clock)
                .build();
    }

    @Nonnull
    @Override
    public Timer timer(@Nonnull final MeterId id, @Nonnull final TimeUnit timeUnit) {
        return (Timer) meters.computeIfAbsent(id, meterId ->
                new DistributionDelegatingTimer(meterId, makeDistribuition(meterId), timeUnit));
    }

    @Override
    public List<Metric> measure(@Nonnull final Predicate<MeterId> filter) {
        return meters.values().stream().filter(meter -> filter.test(meter.getId()))
                .flatMap(measurable -> measurable.measure().stream())
                .collect(toList());
    }
}
