package io.github.anycollect.metric;

@Deprecated
public interface Point {
    static Point of(PointId id, double value, long timestamp) {
        return new ImmutablePoint(id, value, timestamp);
    }

    PointId getId();

    double getValue();

    long getTimestamp();
}
