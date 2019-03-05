package io.github.anycollect.metric.prepared;

import io.github.anycollect.metric.Measurement;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.metric.frame.MetricFrame;

import javax.annotation.Nonnull;
import java.util.List;

final class CompiledMetric implements Metric {
    private final MetricFrame data;
    private final List<? extends Measurement> measurements;
    private final long timestamp;

    CompiledMetric(@Nonnull final MetricFrame data,
                   @Nonnull final List<? extends Measurement> measurements,
                   final long timestamp) {
        this.data = data;
        this.measurements = measurements;
        this.timestamp = timestamp;
    }

    @Nonnull
    @Override
    public String getKey() {
        return data.getKey();
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Nonnull
    @Override
    public List<? extends Measurement> getMeasurements() {
        return measurements;
    }

    @Nonnull
    @Override
    public Tags getTags() {
        return data.getTags();
    }

    @Nonnull
    @Override
    public Tags getMeta() {
        return data.getMeta();
    }
}
