package io.github.anycollect.metric.prepared;

import io.github.anycollect.metric.BaseBuilder;
import io.github.anycollect.metric.Stat;
import io.github.anycollect.metric.Type;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public final class PreparedMetricFamilyBuilder extends BaseBuilder<PreparedMetricFamilyBuilder> {
    private final List<PreparedMeasurement> preparedMeasurements = new ArrayList<>();

    @Override
    protected PreparedMetricFamilyBuilder self() {
        return this;
    }

    public PreparedMetricFamilyBuilder key(@Nonnull final String key) {
        return super.key(key);
    }

    @Override
    public PreparedMetricFamilyBuilder key(@Nonnull final String... keyParts) {
        return super.key(keyParts);
    }

    public PreparedMetricFamilyBuilder measurement(@Nonnull final Stat stat,
                                                   @Nonnull final Type type,
                                                   @Nonnull final String unit) {
        preparedMeasurements.add(new ImmutablePreparedMeasurement(new MeasurementData(stat, type, unit)));
        return this;
    }

    public PreparedMetricFamily build() {
        MetricFamilyData data = new MetricFamilyData(getKey(),
                getTagsBuilder().build(),
                getMetaBuilder().build());
        return new ImmutablePreparedMetricFamily(data, preparedMeasurements);
    }
}
