package io.github.anycollect.metric.prepared;

import io.github.anycollect.metric.Measurement;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.frame.MetricFrame;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

final class ImmutablePreparedMetric implements PreparedMetric {
    private final MetricFrame data;
    private final List<PreparedMeasurement> preparedMeasurements;

    ImmutablePreparedMetric(@Nonnull final MetricFrame data) {
        this.data = data;
        this.preparedMeasurements = data.getMeasurements().stream()
                .map(ImmutablePreparedMeasurement::new)
                .collect(Collectors.toList());
    }

    @Override
    public Metric compile(final long timestamp, final double value) {
        if (preparedMeasurements.size() != 1) {
            throw new IllegalArgumentException("expected " + preparedMeasurements.size() + " values");
        }
        List<Measurement> measurements = Collections.singletonList(preparedMeasurements.get(0).compile(value));
        return new CompiledMetric(data, measurements, timestamp);
    }

    @Override
    public Metric compile(final long timestamp, final double... values) {
        if (preparedMeasurements.size() != values.length) {
            throw new IllegalArgumentException("expected " + preparedMeasurements.size() + " values, "
                    + "given: " + values.length);
        }
        List<Measurement> measurements = new ArrayList<>(preparedMeasurements.size());
        for (int i = 0; i < preparedMeasurements.size(); i++) {
            Measurement measurement = preparedMeasurements.get(i).compile(values[i]);
            measurements.add(measurement);
        }
        return new CompiledMetric(data, measurements, timestamp);
    }
}
