package io.github.anycollect.metric.prepared;

import io.github.anycollect.metric.Measurement;
import io.github.anycollect.metric.Stat;
import io.github.anycollect.metric.Type;
import io.github.anycollect.metric.frame.MeasurementFrame;

import javax.annotation.Nonnull;

final class CompiledMeasurement implements Measurement {
    private final MeasurementFrame data;
    private final double value;

    CompiledMeasurement(@Nonnull final MeasurementFrame data, final double value) {
        this.data = data;
        this.value = value;
    }

    @Nonnull
    @Override
    public Stat getStat() {
        return data.getStat();
    }

    @Nonnull
    @Override
    public Type getType() {
        return data.getType();
    }

    @Nonnull
    @Override
    public String getUnit() {
        return data.getUnit();
    }

    @Override
    public double getValue() {
        return value;
    }
}
