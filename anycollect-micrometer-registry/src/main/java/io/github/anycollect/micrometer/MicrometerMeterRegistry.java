package io.github.anycollect.micrometer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.jackson.AnyCollectModule;
import io.github.anycollect.metric.*;
import io.github.anycollect.metric.Counter;
import io.github.anycollect.metric.MeterRegistry;
import io.github.anycollect.metric.Tag;
import io.github.anycollect.metric.Tags;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.Gauge;
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
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;

public final class MicrometerMeterRegistry extends PushMeterRegistry implements MeterRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(MicrometerMeterRegistry.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

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
    public Counter counter(@Nonnull final MeterId meterId) {
        String description = getDescription(meterId);
        return ((MicrometerCounter) io.micrometer.core.instrument.Counter.builder(meterId.getKey())
                .tags(convertTags(meterId.getTags()))
                .description(description)
                .register(this)).getAdapter();
    }

    @Override
    public io.github.anycollect.metric.DistributionSummary summary(@Nonnull final MeterId id,
                                                                   @Nonnull final double... percentiles) {
        String description = getDescription(id);
        DistributionSummary summary = DistributionSummary.builder(id.getKey())
                .tags(convertTags(id.getTags()))
                .description(description)
                .percentilePrecision(1)
                .publishPercentiles(percentiles)
                .publishPercentileHistogram(false)
                .scale(1.0)
                .minimumExpectedValue(1L)
                .maximumExpectedValue(Long.MAX_VALUE)
                .register(this);
        return new MicrometerDistributionSummary(summary, id, anyClock).getAdapter();
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
            if (!tag.getKey().equals(CommonTags.METRIC_KEY.getKey())) {
                converted.add(io.micrometer.core.instrument.Tag.of(tag.getKey(), tag.getValue()));
            }
        }
        return converted;
    }

    @Override
    protected void publish() {
        getMeters().stream().flatMap(this::convert).forEach(this::dispatch);
    }

    private Stream<Metric> convert(final Meter meter) {
        if (meter instanceof MicrometerCounter) {
            return ((MicrometerCounter) meter).getAdapter().measure();
        }
        if (meter instanceof MicrometerDistributionSummary) {
            return ((MicrometerDistributionSummary) meter).getAdapter().measure();
        }
        if (meter instanceof MicrometerGauge) {
            return ((MicrometerGauge) meter).getAdapter().measure();
        }
        return Stream.empty();
    }

    private void dispatch(final Metric metric) {
        dispatcher.dispatch(metric);
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
        return new MicrometerGauge(gauge, convertId(id), anyClock);
    }

    @Override
    protected MicrometerCounter newCounter(@Nonnull final Meter.Id id) {
        ImmutableMeterId meterId = convertId(id);
        return new MicrometerCounter(new CumulativeCounter(id), meterId, anyClock);
    }

    private ImmutableMeterId convertId(@Nonnull final Meter.Id id) {
        return new ImmutableMeterId.Builder()
                .key(id.getName())
                .concatTags(extractTags(id))
                .concatMeta(extractMetaTags(id))
                .build();
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
    protected DistributionSummary newDistributionSummary(
            @Nonnull final Meter.Id id,
            @Nonnull final DistributionStatisticConfig distributionStatisticConfig,
            final double scale) {
        DistributionSummary summary = new StepDistributionSummary(id, clock, distributionStatisticConfig, scale,
                config.step().toMillis(), false);
        return new MicrometerDistributionSummary(summary, convertId(id), anyClock);
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
}
