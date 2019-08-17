package io.github.anycollect.meter.impl;

import io.github.anycollect.core.api.Reader;
import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.extensions.annotations.InstanceId;
import io.github.anycollect.meter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.*;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

import static java.util.stream.Collectors.toList;

@Extension(name = AnyCollectMeterRegistry.NAME, contracts = {MeterRegistry.class, Reader.class})
public final class AnyCollectMeterRegistry extends MeterRegistry implements Reader, Lifecycle {
    public static final String NAME = "MeterRegistry";
    private static final Logger LOG = LoggerFactory.getLogger(AnyCollectMeterRegistry.class);
    private static final double[] QUANTILES = new double[]{0.5, 0.75, 0.9, 0.95, 0.99, 0.999};
    private final ConcurrentMap<MeterId, Meter> meters = new ConcurrentHashMap<>();
    private final Clock clock = Clock.getDefault();
    private final String id;
    private final AnyCollectMeterRegistryConfig config;
    private final ScheduledExecutorService scheduler;

    @ExtCreator
    public AnyCollectMeterRegistry(@ExtConfig(optional = true) @Nullable final AnyCollectMeterRegistryConfig config,
                                   @InstanceId @Nonnull final String id) {
        this.id = id;
        this.config = config != null ? config : AnyCollectMeterRegistryConfig.DEFAULT;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    @Nonnull
    @Override
    protected <T> Gauge gauge(@Nonnull final MeterId id,
                              @Nonnull final T obj,
                              @Nonnull final ToDoubleFunction<T> value) {
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
    protected Counter counter(@Nonnull final MeterId id) {
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
    protected <T> FunctionCounter counter(@Nonnull final MeterId id,
                                          @Nonnull final T obj,
                                          @Nonnull final ToLongFunction<T> value) {
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
    protected Distribution distribution(@Nonnull final MeterId id) {
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
    protected Timer timer(@Nonnull final MeterId id, @Nonnull final TimeUnit timeUnit) {
        return (Timer) meters.computeIfAbsent(id, meterId ->
                new DistributionDelegatingTimer(meterId, makeDistribuition(meterId), timeUnit));
    }

    @Override
    public void start(@Nonnull final Dispatcher dispatcher) {
        scheduler.scheduleAtFixedRate(() -> {
            dispatcher.dispatch(meters.values().stream()
                    .flatMap(measurable -> measurable.measure().stream())
                    .collect(toList()));
        }, 0L, 10, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        scheduler.shutdownNow();
        LOG.info("{}({}) has been successfully stopped", id, NAME);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void init() {
        LOG.info("{} has been successfully initialised", NAME);
    }

    @Override
    public void destroy() {
        LOG.info("{}({}) has been successfully destroyed", id, NAME);
    }
}
