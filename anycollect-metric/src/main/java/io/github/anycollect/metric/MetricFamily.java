package io.github.anycollect.metric;

import io.github.anycollect.metric.prepared.PreparedMetricFamilyBuilder;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public interface MetricFamily {
    static PreparedMetricFamilyBuilder prepare() {
        return new PreparedMetricFamilyBuilder();
    }

    static MetricFamily of(@Nonnull String key,
                           @Nonnull Tags tags,
                           @Nonnull Tags meta,
                           @Nonnull Measurement measurement,
                           long timestamp) {
        return of(key, tags, meta, Collections.singletonList(measurement), timestamp);
    }

    static MetricFamily of(@Nonnull String key,
                           @Nonnull Tags tags,
                           @Nonnull Tags meta,
                           @Nonnull List<Measurement> measurements,
                           long timestamp) {
        return new ImmutableMetricFamily(
                key, timestamp, measurements, tags, meta
        );
    }

    static MetricFamily empty(@Nonnull MeterId id, long timestamp) {
        return of(id, Collections.emptyList(), timestamp);
    }

    static MetricFamily of(@Nonnull MeterId id, Measurement measurement, long timestamp) {
        return of(id, Collections.singletonList(measurement), timestamp);
    }

    static MetricFamily of(@Nonnull MeterId id, List<Measurement> measurements, long timestamp) {
        return new ImmutableMetricFamily(id.getKey(), timestamp,
                measurements, id.getTags(), id.getMetaTags());
    }

    @Nonnull
    String getKey();

    long getTimestamp();

    @Nonnull
    List<? extends Measurement> getMeasurements();

    @Nonnull
    Tags getTags();

    @Nonnull
    Tags getMeta();

    default boolean isEmpty() {
        return getMeasurements().isEmpty();
    }
}
