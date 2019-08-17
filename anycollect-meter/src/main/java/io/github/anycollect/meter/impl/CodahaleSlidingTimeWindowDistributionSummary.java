package io.github.anycollect.meter.impl;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Reservoir;
import com.codahale.metrics.SlidingTimeWindowArrayReservoir;
import com.codahale.metrics.Snapshot;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.meter.api.MeterId;
import io.github.anycollect.metric.*;
import lombok.Builder;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CodahaleSlidingTimeWindowDistributionSummary extends AbstractMeter implements DistributionMeter {
    private final Clock clock;
    private final Histogram histogram;
    private final double[] quantiles;
    private final List<Metric> ids;

    @Builder
    public CodahaleSlidingTimeWindowDistributionSummary(@Nonnull final MeterId id,
                                                        final int window,
                                                        @Nonnull final double[] quantiles,
                                                        @Nonnull final String prefix,
                                                        @Nonnull final Tags tags,
                                                        @Nonnull final Tags meta,
                                                        @Nonnull final Clock clock) {
        super(id);
        this.clock = clock;
        this.quantiles = quantiles;
        Reservoir reservoir = new SlidingTimeWindowArrayReservoir(window, TimeUnit.SECONDS);
        histogram = new Histogram(reservoir);
        this.ids = new ArrayList<>();
        Metric.Factory factory = Metric.builder()
                .key(id.getKey().withPrefix(prefix))
                .tags(tags.concat(id.getTags()))
                .meta(meta.concat(id.getMeta()));
        this.ids.add(factory.max(id.getUnit()));
        this.ids.add(factory.mean(id.getUnit()));
        this.ids.add(factory.std(id.getUnit()));
        for (double quantile : quantiles) {
            this.ids.add(factory.percentile(quantile, id.getUnit()));
        }
    }

    @Override
    public void record(final long amount) {
        histogram.update(amount);
    }

    @Nonnull
    @Override
    public List<Sample> measure() {
        Snapshot snapshot = histogram.getSnapshot();
        long timestamp = clock.wallTime();
        List<Sample> samples = new ArrayList<>();
        samples.add(ids.get(0).sample(snapshot.getMax(), timestamp));
        samples.add(ids.get(1).sample(snapshot.getMean(), timestamp));
        samples.add(ids.get(2).sample(snapshot.getStdDev(), timestamp));
        for (int bucket = 0; bucket < quantiles.length; bucket++) {
            samples.add(ids.get(3 + bucket).sample(snapshot.getValue(quantiles[bucket]), timestamp));
        }
        return samples;
    }
}
