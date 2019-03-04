package io.github.anycollect.metric;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Deprecated
@Getter
@ToString
@EqualsAndHashCode
public final class ImmutablePoint implements Point {
    private final PointId id;
    private final double value;
    private final long timestamp;

    public ImmutablePoint(final PointId id, final double value, final long timestamp) {
        Objects.requireNonNull(id, "id must not be null");
        this.id = id;
        this.value = value;
        this.timestamp = timestamp;
    }
}
