package io.github.anycollect.metric;

import io.github.anycollect.metric.prepared.PreparedMetricBuilder;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public interface Metric {
    static PreparedMetricBuilder prepare() {
        return new PreparedMetricBuilder();
    }

    static Metric of(@Nonnull String key,
                     @Nonnull Tags tags,
                     @Nonnull Tags meta,
                     @Nonnull Measurement measurement,
                     long timestamp) {
        return of(key, tags, meta, Collections.singletonList(measurement), timestamp);
    }

    static Metric of(@Nonnull String key,
                     @Nonnull Tags tags,
                     @Nonnull Tags meta,
                     @Nonnull List<Measurement> measurements,
                     long timestamp) {
        return new ImmutableMetric(
                key, timestamp, measurements, tags, meta
        );
    }

    static Metric empty(@Nonnull MeterId id, long timestamp) {
        return of(id, Collections.emptyList(), timestamp);
    }

    static Metric of(@Nonnull MeterId id, Measurement measurement, long timestamp) {
        return of(id, Collections.singletonList(measurement), timestamp);
    }

    static Metric of(@Nonnull MeterId id, List<Measurement> measurements, long timestamp) {
        return new ImmutableMetric(id.getKey(), timestamp,
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

    default int size() {
        return getMeasurements().size();
    }
}
