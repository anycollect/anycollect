package io.github.anycollect.metric.prepared;

import io.github.anycollect.metric.Measurement;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.metric.frame.MetricFrame;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

final class CompiledMetric implements Metric {
    private final MetricFrame frame;
    private final List<CompiledMeasurement> measurements;
    private final long timestamp;

    CompiledMetric(@Nonnull final MetricFrame frame,
                   @Nonnull final List<CompiledMeasurement> measurements,
                   final long timestamp) {
        this.frame = frame;
        this.measurements = Collections.unmodifiableList(measurements);
        this.timestamp = timestamp;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Nonnull
    @Override
    public Tags getTags() {
        return frame.getTags();
    }

    @Nonnull
    @Override
    public Tags getMeta() {
        return frame.getMeta();
    }

    @Nonnull
    @Override
    public MetricFrame getFrame() {
        return frame;
    }

    @Override
    public int size() {
        return measurements.size();
    }

    @Nonnull
    @Override
    public Metric reframe(@Nonnull final MetricFrame frame) {
        return new CompiledMetric(frame, measurements, timestamp);
    }

    @Nonnull
    @Override
    public List<? extends Measurement> getMeasurements() {
        return measurements;
    }

    @Nonnull
    @Override
    public String getKey() {
        return frame.getKey();
    }

    @Override
    public String toString() {
        return Metric.toString(this);
    }
}
