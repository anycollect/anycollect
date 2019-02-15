package io.github.anycollect.metric;

import javax.annotation.Nonnull;

public interface MetricId extends Id {
    static ImmutableMetricId.Builder key(@Nonnull String key) {
        return new ImmutableMetricId.Builder().key(key);
    }

    static ImmutableMetricId.Builder builder() {
        return new ImmutableMetricId.Builder();
    }

    default String getKey() {
        return getTagValue(CommonTags.METRIC_KEY.getKey());
    }

    default Stat getStat() {
        return Stat.parse(getTagValue(CommonTags.STAT.getKey()));
    }

    default Type getType() {
        return Type.parse(getTagValue(CommonTags.METRIC_TYPE.getKey()));
    }

    default String getUnit() {
        return getTagValue(CommonTags.UNIT.getKey());
    }
}
