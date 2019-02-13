package io.github.anycollect.metric;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
@EqualsAndHashCode
public final class ImmutableMetric implements Metric {
    private final MetricId id;
    private final double value;
    private final long timestamp;

    public ImmutableMetric(final MetricId id, final double value, final long timestamp) {
        Objects.requireNonNull(id, "id must not be null");
        this.id = id;
        this.value = value;
        this.timestamp = timestamp;
    }
}
