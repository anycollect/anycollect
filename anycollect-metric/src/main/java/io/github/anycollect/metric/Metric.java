package io.github.anycollect.metric;

public interface Metric {
    static Metric of(MetricId id, double value, long timestamp) {
        return new ImmutableMetric(id, value, timestamp);
    }

    MetricId getId();

    double getValue();

    long getTimestamp();
}
