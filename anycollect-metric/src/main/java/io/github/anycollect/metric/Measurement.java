package io.github.anycollect.metric;

import io.github.anycollect.metric.frame.MeasurementFrame;

import javax.annotation.Nonnull;

public interface Measurement {
    static Measurement counter(double value, String unit) {
        return new ImmutableMeasurement(Stat.value(), Type.COUNTER, unit, value);
    }

    static Measurement gauge(double value, String unit) {
        return new ImmutableMeasurement(Stat.value(), Type.GAUGE, unit, value);
    }

    static Measurement mean(double value, String unit) {
        return new ImmutableMeasurement(Stat.mean(), Type.GAUGE, unit, value);
    }

    static Measurement max(double value, String unit) {
        return new ImmutableMeasurement(Stat.max(), Type.GAUGE, unit, value);
    }

    static Measurement std(double value, String unit) {
        return new ImmutableMeasurement(Stat.std(), Type.GAUGE, unit, value);
    }

    static Measurement percentile(final double percentile, double value, String unit) {
        return new ImmutableMeasurement(Stat.percentile(percentile), Type.GAUGE, unit, value);
    }

    static Measurement percentile(final int num, double value, String unit) {
        return new ImmutableMeasurement(Stat.percentile(num), Type.GAUGE, unit, value);
    }

    @Nonnull
    MeasurementFrame getFrame();

    @Nonnull
    default Stat getStat() {
        return getFrame().getStat();
    }

    @Nonnull
    default Type getType() {
        return getFrame().getType();
    }

    @Nonnull
    default String getUnit() {
        return getFrame().getUnit();
    }

    double getValue();
}
