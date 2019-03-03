package io.github.anycollect.meter.registry;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.SlidingTimeWindowReservoir;
import com.codahale.metrics.Snapshot;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.metric.*;
import io.github.anycollect.metric.prepared.PreparedMetricFamily;
import io.github.anycollect.metric.prepared.PreparedMetricFamilyBuilder;
import lombok.Builder;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CodahaleSlidingTimeWindowDistributionSummary extends AbstractMeter implements Distribution, Measurable {
    private final Clock clock;
    private final Histogram histogram;
    private final double[] quantiles;
    private final PreparedMetricFamily preparedHistogram;

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
        SlidingTimeWindowReservoir reservoir = new SlidingTimeWindowReservoir(window, TimeUnit.SECONDS);
        histogram = new Histogram(reservoir);
        PreparedMetricFamilyBuilder builder = MetricFamily.prepare()
                .key(prefix, id.getKey())
                .concatTags(tags)
                .concatTags(id.getTags())
                .concatMeta(meta)
                .concatMeta(id.getMetaTags());
        builder.measurement(Stat.max(), Type.GAUGE, id.getUnit())
                .measurement(Stat.mean(), Type.GAUGE, id.getUnit())
                .measurement(Stat.std(), Type.GAUGE, id.getUnit());
        for (double quantile : quantiles) {
            builder.measurement(Stat.percentile(quantile), Type.GAUGE, id.getUnit());
        }
        this.preparedHistogram = builder.build();
    }

    @Override
    public void record(final long amount) {
        histogram.update(amount);
    }

    @Nonnull
    @Override
    public List<MetricFamily> measure() {
        Snapshot snapshot = histogram.getSnapshot();
        double[] values = new double[3 + quantiles.length];
        values[0] = snapshot.getMax();
        values[1] = snapshot.getMean();
        values[2] = snapshot.getStdDev();
        for (int bucket = 0; bucket < quantiles.length; bucket++) {
            values[3 + bucket] = snapshot.getValue(quantiles[bucket]);
        }
        return Collections.singletonList(preparedHistogram.compile(clock.wallTime(), values));
    }
}
