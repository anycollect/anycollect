package io.github.anycollect.metric;

import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.joining;

@Getter
public final class ImmutableMetricFamily implements MetricFamily {
    private final String key;
    private final long timestamp;
    private final List<Measurement> measurements;
    private final Tags tags;
    private final Tags meta;

    public ImmutableMetricFamily(@Nonnull final String key,
                                 final long timestamp,
                                 @Nonnull final List<Measurement> measurements,
                                 @Nonnull final Tags tags,
                                 @Nonnull final Tags meta) {
        this.key = key;
        this.timestamp = timestamp;
        this.measurements = Collections.unmodifiableList(new ArrayList<>(measurements));
        this.tags = tags;
        this.meta = meta;
    }

    @Override
    public String toString() {
        return key + ";" + (!tags.isEmpty() ? tags + ";" : "") + measurements.stream()
                .map(Measurement::toString)
                .collect(joining(","));
    }
}
