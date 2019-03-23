package io.github.anycollect.metric.prepared;

import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.frame.MetricFrame;

import javax.annotation.Nonnull;

public interface PreparedMetric {
    static PreparedMetric create(@Nonnull MetricFrame frame) {
        return new ImmutablePreparedMetric(frame);
    }

    Metric compile(long timestamp, double value);

    Metric compile(long timestamp, double... values);
}
