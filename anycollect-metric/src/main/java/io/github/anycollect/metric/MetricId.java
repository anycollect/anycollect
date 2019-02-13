package io.github.anycollect.metric;

import javax.annotation.Nonnull;

public interface MetricId extends Id {
    static ImmutableMetricId.Builder key(@Nonnull String key) {
        return new ImmutableMetricId.Builder().key(key);
    }

    static ImmutableMetricId.Builder builder() {
        return new ImmutableMetricId.Builder();
    }

    String getKey();

    Stat getStat();

    Type getType();

    String getUnit();
}
