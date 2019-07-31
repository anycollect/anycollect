package io.github.anycollect.metric;

import lombok.EqualsAndHashCode;

import javax.annotation.Nonnull;

@EqualsAndHashCode
public final class DoubleSample implements Sample {
    private final Metric id;
    private final double value;
    private final long timestamp;

    DoubleSample(@Nonnull final Metric id, final double value, final long timestamp) {
        this.id = id;
        this.value = value;
        this.timestamp = timestamp;
    }

    @Nonnull
    @Override
    public Metric getMetric() {
        return id;
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return Sample.toString(this);
    }
}
