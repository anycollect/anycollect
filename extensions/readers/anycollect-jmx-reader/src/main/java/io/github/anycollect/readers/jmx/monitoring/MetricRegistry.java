package io.github.anycollect.readers.jmx.monitoring;

import io.github.anycollect.metric.MetricId;

import javax.annotation.Nonnull;
import java.util.function.DoubleSupplier;

public interface MetricRegistry {
    void register(@Nonnull MetricId id, @Nonnull DoubleSupplier value);

    static MetricRegistry noop() {
        return new NoopMetricRegistry();
    }
}
