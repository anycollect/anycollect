package io.github.anycollect.metric.prepared;

import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.frame.MeasurementFrame;
import io.github.anycollect.metric.frame.MetricFrame;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class ImmutablePreparedMetric implements PreparedMetric {
    private final MetricFrame frame;
    private final List<MeasurementFrame> measurementFrames;

    ImmutablePreparedMetric(@Nonnull final MetricFrame frame,
                            @Nonnull final List<MeasurementFrame> measurementFrames) {
        this.frame = frame;
        this.measurementFrames = Collections.unmodifiableList(new ArrayList<>(measurementFrames));
    }

    @Override
    public Metric compile(final long timestamp, final double value) {
        if (measurementFrames.size() != 1) {
            throw new IllegalArgumentException("expected " + measurementFrames.size() + " values");
        }
        List<CompiledMeasurement> measurements = Collections.singletonList(
                new CompiledMeasurement(measurementFrames.get(0), value));
        return new CompiledMetric(frame, measurements, timestamp);
    }

    @Override
    public Metric compile(final long timestamp, final double... values) {
        if (measurementFrames.size() != values.length) {
            throw new IllegalArgumentException("expected " + measurementFrames.size() + " values, "
                    + "given: " + values.length);
        }
        List<CompiledMeasurement> measurements = new ArrayList<>(measurementFrames.size());
        for (int i = 0; i < measurementFrames.size(); i++) {
            CompiledMeasurement measurement = new CompiledMeasurement(measurementFrames.get(i), values[i]);
            measurements.add(measurement);
        }
        return new CompiledMetric(frame, measurements, timestamp);
    }
}
