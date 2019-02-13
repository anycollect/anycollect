package io.github.anycollect.metric;

public interface MetricId extends Id {
    static ImmutableMetricId.Builder builder() {
        return new ImmutableMetricId.Builder();
    }

    String getKey();

    Stat getStat();

    Type getType();

    String getUnit();
}
