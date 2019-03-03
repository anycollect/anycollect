package io.github.anycollect.metric.prepared;

import io.github.anycollect.metric.Measurement;

final class ImmutablePreparedMeasurement implements PreparedMeasurement {
    private final MeasurementData data;

    ImmutablePreparedMeasurement(final MeasurementData data) {
        this.data = data;
    }

    @Override
    public Measurement compile(final double value) {
        return new CompiledMeasurement(data, value);
    }
}
