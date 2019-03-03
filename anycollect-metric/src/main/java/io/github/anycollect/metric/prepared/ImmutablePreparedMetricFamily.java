package io.github.anycollect.metric.prepared;

import io.github.anycollect.metric.Measurement;
import io.github.anycollect.metric.MetricFamily;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class ImmutablePreparedMetricFamily implements PreparedMetricFamily {
    private final MetricFamilyData data;
    private final List<PreparedMeasurement> preparedMeasurements;

    ImmutablePreparedMetricFamily(@Nonnull final MetricFamilyData data,
                                         @Nonnull final List<PreparedMeasurement> preparedMeasurements) {
        this.data = data;
        this.preparedMeasurements = new ArrayList<>(preparedMeasurements);
    }

    @Override
    public MetricFamily compile(final long timestamp, final double value) {
        if (preparedMeasurements.size() != 1) {
            throw new IllegalArgumentException("expected " + preparedMeasurements.size() + " values");
        }
        List<Measurement> measurements = Collections.singletonList(preparedMeasurements.get(0).compile(value));
        return new CompiledMetricFamily(data, measurements, timestamp);
    }

    @Override
    public MetricFamily compile(final long timestamp, final double... values) {
        if (preparedMeasurements.size() != values.length) {
            throw new IllegalArgumentException("expected " + preparedMeasurements.size() + " values, "
                    + "given: " + values.length);
        }
        List<Measurement> measurements = new ArrayList<>(preparedMeasurements.size());
        for (int i = 0; i < preparedMeasurements.size(); i++) {
            Measurement measurement = preparedMeasurements.get(i).compile(values[i]);
            measurements.add(measurement);
        }
        return new CompiledMetricFamily(data, measurements, timestamp);
    }
}
