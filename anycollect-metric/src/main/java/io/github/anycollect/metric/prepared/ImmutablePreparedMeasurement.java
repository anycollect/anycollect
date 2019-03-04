package io.github.anycollect.metric.prepared;

import io.github.anycollect.metric.Measurement;

final class ImmutablePreparedMeasurement implements PreparedMeasurement {
    private final MeasurementFrame data;

    ImmutablePreparedMeasurement(final MeasurementFrame data) {
        this.data = data;
    }

    @Override
    public Measurement compile(final double value) {
        return new CompiledMeasurement(data, value);
    }
}
