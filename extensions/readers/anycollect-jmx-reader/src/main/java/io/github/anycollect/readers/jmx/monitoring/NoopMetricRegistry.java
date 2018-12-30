package io.github.anycollect.readers.jmx.monitoring;

import io.github.anycollect.metric.MetricId;

import javax.annotation.Nonnull;
import java.util.function.DoubleSupplier;

public final class NoopMetricRegistry implements MetricRegistry {
    @Override
    public void register(@Nonnull final MetricId id, @Nonnull final DoubleSupplier value) {
    }
}
