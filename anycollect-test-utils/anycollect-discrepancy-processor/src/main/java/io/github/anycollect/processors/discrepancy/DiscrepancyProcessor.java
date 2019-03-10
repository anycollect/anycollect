package io.github.anycollect.processors.discrepancy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.anycollect.core.api.Processor;
import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.extensions.annotations.InstanceId;
import io.github.anycollect.metric.Measurement;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;
import java.util.regex.Pattern;

@Extension(name = DiscrepancyProcessor.NAME, point = Processor.class)
public final class DiscrepancyProcessor implements Processor, Lifecycle {
    public static final String NAME = "DiscrepancyProcessor";
    private static final Logger LOG = LoggerFactory.getLogger(DiscrepancyProcessor.class);
    private final Config config;
    private volatile Dispatcher dispatcher;
    private final ConcurrentMap<String, LongAdder> counts = new ConcurrentHashMap<>();
    private final double[] buckets = new double[]{1, 2, 5, 10, 20, 100, 500, 1000, Double.POSITIVE_INFINITY};
    private final ConcurrentMap<String, CumulativeFixedBoundaryHistogram> periodDiscrepancies
            = new ConcurrentHashMap<>(); // group, buckets
    private final double[] clientBuckets = new double[]{1, 2, 5, 10, 20, Double.POSITIVE_INFINITY};
    private final ConcurrentMap<String, CumulativeFixedBoundaryHistogram> measureDiscrepancies
            = new ConcurrentHashMap<>(); // group, buckets
    private final ConcurrentMap<String, Long> lastTimestamp = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final String id;

    @ExtCreator
    public DiscrepancyProcessor(@ExtConfig @Nonnull final Config config,
                                @InstanceId @Nonnull final String id) {
        this.config = config;
        scheduler.scheduleAtFixedRate(this::publish, 0L, 10, TimeUnit.SECONDS);
        this.id = id;
    }

    @Override
    public void start(@Nonnull final Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    private void publish() {
        if (dispatcher == null) {
            return;
        }
        for (Map.Entry<String, LongAdder> entry : counts.entrySet()) {
            Measurement counter = Measurement.counter(entry.getValue().doubleValue(), "hits");
            long timestamp = System.currentTimeMillis();
            Metric family
                    = Metric.of("counting", Tags.of("group", entry.getKey()), Tags.empty(), counter, timestamp);
            dispatcher.dispatch(family);
        }
        for (CumulativeFixedBoundaryHistogram histogram : periodDiscrepancies.values()) {
            dispatcher.dispatch(histogram.measure());
        }
        for (CumulativeFixedBoundaryHistogram histogram : measureDiscrepancies.values()) {
            dispatcher.dispatch(histogram.measure());
        }
    }

    @Override
    public void submit(@Nonnull final List<Metric> sources) {
        if (dispatcher == null) {
            return;
        }
        for (Metric source : sources) {
            for (Rule rule : config.rules) {
                if (rule.filter.matcher(source.getKey()).matches()) {
                    counts.computeIfAbsent(rule.group, group -> new LongAdder()).increment();
                    CumulativeFixedBoundaryHistogram histogram
                            = periodDiscrepancies.computeIfAbsent(rule.group,
                            group -> new CumulativeFixedBoundaryHistogram(
                                    "discrepancy.period",
                                    Tags.of("group", group),
                                    Tags.empty(),
                                    buckets
                            )
                    );
                    Long lastTime = lastTimestamp.get(source.getKey());
                    lastTimestamp.put(source.getKey(), source.getTimestamp());
                    long periodInMillis = TimeUnit.SECONDS.toMillis(rule.period);
                    if (lastTime != null) {
                        int d = (int) (100 * (Math.abs(source.getTimestamp() - lastTime - periodInMillis))
                                / periodInMillis);
                        histogram.record(d);
                    }
                    CumulativeFixedBoundaryHistogram clientHistogram
                            = measureDiscrepancies.computeIfAbsent(rule.group,
                            group -> new CumulativeFixedBoundaryHistogram(
                                    "discrepancy.measure",
                                    Tags.of("group", group),
                                    Tags.empty(),
                                    clientBuckets
                            )
                    );
                    for (Measurement measurement : source.getMeasurements()) {
                        double expectedTimestamp = measurement.getValue();
                        long actualTimestamp = source.getTimestamp();
                        long timeDiff = (long) Math.abs(expectedTimestamp - actualTimestamp);
                        clientHistogram.record(TimeUnit.MILLISECONDS.toSeconds(timeDiff));
                    }
                }
            }
        }
    }

    @Override
    public void init() {
        LOG.info("{} has been successfully initialized", NAME);
    }

    @Override
    public void destroy() {
        LOG.info("{} has been successfully destroyed", NAME);
    }

    @Override
    public String getId() {
        return id;
    }

    public static class Config {
        private final List<Rule> rules;

        @JsonCreator
        public Config(@JsonProperty("rules") @Nonnull final List<Rule> rules) {
            this.rules = rules;
        }
    }

    public static class Rule {
        private final Pattern filter;
        private final String group;
        private final int period;

        @JsonCreator
        public Rule(@JsonProperty("filter") @Nonnull final String filter,
                    @JsonProperty("group") @Nonnull final String group,
                    @JsonProperty("period") final int period) {
            this.group = group;
            this.filter = Pattern.compile(filter);
            this.period = period;
        }
    }
}
