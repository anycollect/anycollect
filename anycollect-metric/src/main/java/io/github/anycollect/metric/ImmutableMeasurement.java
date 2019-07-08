package io.github.anycollect.metric;

import io.github.anycollect.metric.frame.ImmutableMeasurementFrame;
import io.github.anycollect.metric.frame.MeasurementFrame;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.annotation.Nonnull;

@Getter
@EqualsAndHashCode
public final class ImmutableMeasurement implements Measurement {
    private final MeasurementFrame frame;
    private final double value;

    public ImmutableMeasurement(@Nonnull final Stat stat,
                                @Nonnull final Type type,
                                @Nonnull final String unit,
                                final double value) {
        this.frame = new ImmutableMeasurementFrame(stat, type, unit);
        this.value = value;
    }

    @Override
    public String toString() {
        return Measurement.toString(this);
    }

    @Nonnull
    @Override
    public MeasurementFrame getFrame() {
        return frame;
    }
}
