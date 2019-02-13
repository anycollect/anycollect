package io.github.anycollect.metric;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface MeterId extends Id {
    static ImmutableMeterId.Builder key(@Nonnull String key) {
        return new ImmutableMeterId.Builder().key(key);
    }

    @Nonnull
    String getKey();

    @Nonnull
    String getUnit();

    default MetricId counter() {
        return counter(null);
    }

    default MetricId max() {
        return max(null);
    }

    default MetricId mean() {
        return mean(null);
    }

    default MetricId value() {
        return value(null);
    }

    default MetricId percentile(int num) {
        return percentile(num, null);
    }

    default MetricId percentile(double percentile) {
        return percentile(percentile, null);
    }

    MetricId counter(@Nullable String unit);

    MetricId max(@Nullable String unit);

    MetricId mean(@Nullable String unit);

    MetricId value(@Nullable String unit);

    MetricId percentile(int num, @Nullable String unit);

    MetricId percentile(double percentile, @Nullable String unit);
}
