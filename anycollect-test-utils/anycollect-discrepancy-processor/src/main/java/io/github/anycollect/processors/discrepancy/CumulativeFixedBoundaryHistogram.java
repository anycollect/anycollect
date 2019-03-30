package io.github.anycollect.processors.discrepancy;

import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.metric.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLongArray;

public final class CumulativeFixedBoundaryHistogram {
    private final double[] buckets;
    private final AtomicLongArray counts;
    private final String key;
    private final Tags tags;
    private final Tags meta;

    public CumulativeFixedBoundaryHistogram(@Nonnull final String key,
                                            @Nonnull final Tags tags,
                                            @Nonnull final Tags meta,
                                            final double[] buckets) {
        this.buckets = buckets;
        this.counts = new AtomicLongArray(buckets.length);
        this.key = key;
        this.tags = tags;
        this.meta = meta;
    }

    public void record(final long amount) {
        for (int bucket = 0; bucket < buckets.length; bucket++) {
            if (amount <= buckets[bucket]) {
                counts.incrementAndGet(bucket);
            }
        }
    }

    public Metric measure() {
        List<Measurement> measurements = new ArrayList<>();
        for (int bucket = 0; bucket < buckets.length; bucket++) {
            Measurement measurement
                    = new ImmutableMeasurement(Stat.le(buckets[bucket]), Type.COUNTER, "", counts.get(bucket));
            measurements.add(measurement);
        }
        return Metric.of(key, tags, meta, measurements, Clock.getDefault().wallTime());
    }
}
