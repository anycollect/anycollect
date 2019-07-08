package io.github.anycollect.metric.prepared;

import io.github.anycollect.metric.Measurement;
import io.github.anycollect.metric.Stat;
import io.github.anycollect.metric.Type;
import io.github.anycollect.metric.frame.MeasurementFrame;

import javax.annotation.Nonnull;

final class CompiledMeasurement implements Measurement {
    private final MeasurementFrame frame;
    private final double value;

    CompiledMeasurement(@Nonnull final MeasurementFrame frame, final double value) {
        this.frame = frame;
        this.value = value;
    }

    @Nonnull
    public MeasurementFrame getFrame() {
        return frame;
    }

    @Nonnull
    @Override
    public Stat getStat() {
        return frame.getStat();
    }

    @Nonnull
    @Override
    public Type getType() {
        return frame.getType();
    }

    @Nonnull
    @Override
    public String getUnit() {
        return frame.getUnit();
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Measurement.toString(this);
    }
}
