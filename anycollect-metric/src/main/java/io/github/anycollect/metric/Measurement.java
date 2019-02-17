package io.github.anycollect.metric;

import javax.annotation.Nonnull;

public interface Measurement {
    static Measurement counter(double value) {
        return new ImmutableMeasurement(Stat.value(), Type.COUNTER, value);
    }

    static Measurement gauge(double value) {
        return new ImmutableMeasurement(Stat.value(), Type.GAUGE, value);
    }

    static Measurement mean(double value) {
        return new ImmutableMeasurement(Stat.mean(), Type.GAUGE, value);
    }

    static Measurement max(double value) {
        return new ImmutableMeasurement(Stat.max(), Type.GAUGE, value);
    }

    static Measurement percentile(final double percentile, double value) {
        return new ImmutableMeasurement(Stat.percentile(percentile), Type.GAUGE, value);
    }

    static Measurement percentile(final int num, double value) {
        return new ImmutableMeasurement(Stat.percentile(num), Type.GAUGE, value);
    }

    @Nonnull
    Stat getStat();

    @Nonnull
    Type getType();

    double getValue();
}
