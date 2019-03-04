package io.github.anycollect.metric.prepared;

import io.github.anycollect.metric.BaseBuilder;
import io.github.anycollect.metric.Stat;
import io.github.anycollect.metric.Type;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public final class PreparedMetricBuilder extends BaseBuilder<PreparedMetricBuilder> {
    private final List<PreparedMeasurement> preparedMeasurements = new ArrayList<>();

    @Override
    protected PreparedMetricBuilder self() {
        return this;
    }

    public PreparedMetricBuilder key(@Nonnull final String key) {
        return super.key(key);
    }

    @Override
    public PreparedMetricBuilder key(@Nonnull final String... keyParts) {
        return super.key(keyParts);
    }

    public PreparedMetricBuilder measurement(@Nonnull final Stat stat,
                                             @Nonnull final Type type,
                                             @Nonnull final String unit) {
        preparedMeasurements.add(new ImmutablePreparedMeasurement(new MeasurementFrame(stat, type, unit)));
        return this;
    }

    public PreparedMetric build() {
        MetricFrame data = new MetricFrame(getKey(),
                getTagsBuilder().build(),
                getMetaBuilder().build());
        return new ImmutablePreparedMetric(data, preparedMeasurements);
    }
}
