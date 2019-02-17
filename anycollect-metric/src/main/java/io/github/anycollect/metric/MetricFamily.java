package io.github.anycollect.metric;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public interface MetricFamily {
    static MetricFamily empty(@Nonnull MeterId id, long timestamp) {
        return of(id, Collections.emptyList(), timestamp);
    }

    static MetricFamily of(@Nonnull MeterId id, Measurement measurement, long timestamp) {
        return of(id, Collections.singletonList(measurement), timestamp);
    }

    static MetricFamily of(@Nonnull MeterId id, List<Measurement> measurements, long timestamp) {
        return new ImmutableMetricFamily(
                id.getKey(), id.getUnit(), timestamp,
                measurements, id.getTags(), id.getMetaTags());
    }

    @Nonnull
    String getKey();

    @Nonnull
    String getUnit();

    long getTimestamp();

    @Nonnull
    List<Measurement> getMeasurements();

    @Nonnull
    Tags getTags();

    @Nonnull
    Tags getMeta();

    default boolean isEmpty() {
        return getMeasurements().isEmpty();
    }
}
