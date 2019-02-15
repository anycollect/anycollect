package io.github.anycollect.metric;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface MeterId extends Id {
    static ImmutableMeterId.Builder key(@Nonnull String key) {
        return new ImmutableMeterId.Builder().key(key);
    }

    @Nonnull
    default String getKey() {
        return getTags().getTagValue(CommonTags.METRIC_KEY.getKey());
    }

    @Nonnull
    default String getUnit() {
        return getTags().getTagValue(CommonTags.UNIT.getKey());
    }

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

    default MetricId counter(@Nullable final String unit) {
        return MetricId.builder()
                .concatTags(getTags())
                .concatMeta(getMetaTags())
                .stat(Stat.value())
                .type(Type.COUNTER)
                .unit(unit == null ? getUnit() : unit)
                .build();
    }

    default MetricId max(@Nullable final String unit) {
        return MetricId.builder()
                .concatTags(getTags())
                .concatMeta(getMetaTags())
                .type(Type.GAUGE)
                .unit(unit == null ? getUnit() : unit)
                .stat(Stat.max())
                .build();
    }

    default MetricId mean(@Nullable final String unit) {
        return MetricId.builder()
                .concatTags(getTags())
                .concatMeta(getMetaTags())
                .type(Type.GAUGE)
                .unit(unit == null ? getUnit() : unit)
                .stat(Stat.mean())
                .build();
    }

    default MetricId value(@Nullable final String unit) {
        return MetricId.builder()
                .concatTags(getTags())
                .concatMeta(getMetaTags())
                .type(Type.GAUGE)
                .unit(unit == null ? getUnit() : unit)
                .stat(Stat.value())
                .build();
    }

    default MetricId percentile(final int num, @Nullable final String unit) {
        return MetricId.builder()
                .concatTags(getTags())
                .concatMeta(getMetaTags())
                .type(Type.GAUGE)
                .unit(unit == null ? getUnit() : unit)
                .stat(Stat.percentile(num))
                .build();
    }

    default MetricId percentile(final double percentile, @Nullable final String unit) {
        return MetricId.builder()
                .concatTags(getTags())
                .concatMeta(getMetaTags())
                .type(Type.GAUGE)
                .unit(unit == null ? getUnit() : unit)
                .stat(Stat.percentile(percentile))
                .build();
    }
}
