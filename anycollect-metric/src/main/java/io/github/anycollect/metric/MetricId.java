package io.github.anycollect.metric;

import javax.annotation.Nonnull;

public interface MetricId extends Id {
    static ImmutableMetricId.Builder key(@Nonnull String key) {
        return new ImmutableMetricId.Builder(key);
    }

    @Nonnull
    String getKey();

    @Nonnull
    String getUnit();

    @Nonnull
    Stat getStat();

    @Nonnull
    Type getType();
}
