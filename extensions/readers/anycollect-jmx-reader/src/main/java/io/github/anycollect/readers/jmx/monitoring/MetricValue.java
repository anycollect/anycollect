package io.github.anycollect.readers.jmx.monitoring;

import java.util.function.DoubleSupplier;

public final class MetricValue implements MetricValueMBean {
    private final DoubleSupplier value;

    public MetricValue(final DoubleSupplier value) {
        this.value = value;
    }

    @Override
    public double getValue() {
        return value.getAsDouble();
    }
}
