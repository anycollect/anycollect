package io.github.anycollect.micrometer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.jackson.AnyCollectModule;
import io.github.anycollect.metric.Counter;
import io.github.anycollect.metric.*;
import io.github.anycollect.metric.MeterRegistry;
import io.github.anycollect.metric.Tag;
import io.github.anycollect.metric.Tags;
import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.cumulative.CumulativeCounter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import io.micrometer.core.instrument.internal.DefaultGauge;
import io.micrometer.core.instrument.push.PushMeterRegistry;
import io.micrometer.core.instrument.push.PushRegistryConfig;
import io.micrometer.core.instrument.step.StepDistributionSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

public final class MicrometerMeterRegistry extends PushMeterRegistry implements MeterRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(MicrometerMeterRegistry.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final ConcurrentMap<MeterId, io.github.anycollect.metric.Meter> meters = new ConcurrentHashMap<>();

    static {
        MAPPER.registerModule(new AnyCollectModule());
    }

    private final Dispatcher dispatcher;
    private final Clock anyClock;
    private final io.micrometer.core.instrument.Clock clock;
    private final PushRegistryConfig config;

    public MicrometerMeterRegistry(final Dispatcher dispatcher, final Clock clock, final PushRegistryConfig config) {
        super(io.github.anycollect.micrometer.Config.DEFAULT, new MicrometerClock(clock));
        this.dispatcher = dispatcher;
        this.clock = new MicrometerClock(clock);
        this.anyClock = clock;
        this.config = config;
    }

    @Override
    public Counter counter(@Nonnull final MeterId id) {
        return (Counter) meters.computeIfAbsent(id, this::registerNewCounter);
    }

    private Counter registerNewCounter(@Nonnull final MeterId meterId) {
        return ((MicrometerCounter) io.micrometer.core.instrument.Counter.builder(meterId.getKey())
                .tags(convertTags(meterId.getTags()))
                .description(getDescription(meterId))
                .register(this)).getMeter();
    }

    private Distribution registerNewSummary(@Nonnull final MeterId id) {
        DistributionSummary summary = DistributionSummary.builder(id.getKey())
                .tags(convertTags(id.getTags()))
                .description(getDescription(id))
                .percentilePrecision(1)
                .publishPercentiles(0.5, 0.75, 0.99, 0.999)
                .publishPercentileHistogram(false)
                .scale(1.0)
                .minimumExpectedValue(1L)
                .maximumExpectedValue(Long.MAX_VALUE)
                .register(this);
        return new MicrometerDistributionSummary(summary, id, anyClock).getMeter();
    }

    @Override
    public Distribution distribution(@Nonnull final MeterId id) {
        return (Distribution)
                meters.computeIfAbsent(id, this::registerNewSummary);
    }

    private String getDescription(@Nonnull final MeterId meterId) {
        String description = null;
        try {
            description = MAPPER.writeValueAsString(meterId.getMetaTags());
        } catch (JsonProcessingException e) {
            LOG.debug("could not convert meta tags to description", e);
        }
        return description;
    }

    private Iterable<io.micrometer.core.instrument.Tag> convertTags(final Tags tags) {
        List<io.micrometer.core.instrument.Tag> converted = new ArrayList<>();
        for (Tag tag : tags) {
            converted.add(io.micrometer.core.instrument.Tag.of(tag.getKey(), tag.getValue()));
        }
        return converted;
    }

    @Override
    protected void publish() {
        getMeters().stream().map(this::convert).filter(Objects::nonNull).forEach(this::dispatch);
    }

    private MetricFamily convert(final Meter meter) {
        if (meter instanceof MeterAdapter) {
            io.github.anycollect.metric.Meter anycollectMeter = ((MeterAdapter) meter).getMeter();
            return anycollectMeter.measure();
        }
        return null;
    }

    private void dispatch(final MetricFamily family) {
        dispatcher.dispatch(family);
    }

    @Override
    protected TimeUnit getBaseTimeUnit() {
        return TimeUnit.MILLISECONDS;
    }

    @Override
    protected DistributionStatisticConfig defaultHistogramConfig() {
        return DistributionStatisticConfig.builder()
                .expiry(config.step())
                .build()
                .merge(DistributionStatisticConfig.DEFAULT);
    }

    @Override
    protected <T> Gauge newGauge(@Nonnull final Meter.Id id,
                                 final T obj,
                                 @Nonnull final ToDoubleFunction<T> valueFunction) {

        DefaultGauge<T> gauge = new DefaultGauge<>(id, obj, valueFunction);
        return new MicrometerGauge(anyClock, gauge, convertId(id));
    }

    @Override
    protected MicrometerCounter newCounter(@Nonnull final Meter.Id id) {
        ImmutableMeterId meterId = convertId(id);
        return new MicrometerCounter(anyClock, new CumulativeCounter(id), meterId);
    }

    @Override
    protected DistributionSummary newDistributionSummary(
            @Nonnull final Meter.Id id,
            @Nonnull final DistributionStatisticConfig distributionStatisticConfig,
            final double scale) {
        DistributionSummary summary = new StepDistributionSummary(id, clock, distributionStatisticConfig, scale,
                config.step().toMillis(), false);
        return new MicrometerDistributionSummary(summary, convertId(id), anyClock);
    }

    private ImmutableMeterId convertId(@Nonnull final Meter.Id id) {
        return new ImmutableMeterId.Builder()
                .key(id.getName())
                .concatTags(extractTags(id))
                .concatMeta(extractMetaTags(id))
                .build();
    }

    private Tags extractTags(final Meter.Id micrometerId) {
        ImmutableTags.Builder builder = Tags.builder();
        for (io.micrometer.core.instrument.Tag tag : micrometerId.getTags()) {
            builder.tag(tag.getKey(), tag.getValue());
        }
        return builder.build();
    }

    private Tags extractMetaTags(final Meter.Id micrometerId) {
        if (micrometerId.getDescription() == null) {
            return Tags.empty();
        }
        try {
            return MAPPER.readValue(micrometerId.getDescription(), Tags.class);
        } catch (IOException e) {
            LOG.debug("could not convert description to meta tags");
        }
        return Tags.empty();
    }

    @Override
    protected LongTaskTimer newLongTaskTimer(@Nonnull final Meter.Id id) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Timer newTimer(@Nonnull final Meter.Id id,
                             @Nonnull final DistributionStatisticConfig distributionStatisticConfig,
                             @Nonnull final PauseDetector pauseDetector) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Meter newMeter(@Nonnull final Meter.Id id,
                             @Nonnull final Meter.Type type,
                             @Nonnull final Iterable<Measurement> measurements) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected <T> FunctionTimer newFunctionTimer(@Nonnull final Meter.Id id,
                                                 @Nonnull final T obj,
                                                 @Nonnull final ToLongFunction<T> countFunction,
                                                 @Nonnull final ToDoubleFunction<T> totalTimeFunction,
                                                 @Nonnull final TimeUnit totalTimeFunctionUnit) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected <T> FunctionCounter newFunctionCounter(@Nonnull final Meter.Id id,
                                                     @Nonnull final T obj,
                                                     @Nonnull final ToDoubleFunction<T> countFunction) {
        throw new UnsupportedOperationException();
    }
}
