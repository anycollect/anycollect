package io.github.anycollect.metric.prepared;

import io.github.anycollect.metric.Measurement;
import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class ImmutablePreparedMetric implements PreparedMetricFamily {
    private final MetricFrame data;
    private final List<PreparedMeasurement> preparedMeasurements;

    ImmutablePreparedMetric(@Nonnull final MetricFrame data,
                            @Nonnull final List<PreparedMeasurement> preparedMeasurements) {
        this.data = data;
        this.preparedMeasurements = new ArrayList<>(preparedMeasurements);
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
