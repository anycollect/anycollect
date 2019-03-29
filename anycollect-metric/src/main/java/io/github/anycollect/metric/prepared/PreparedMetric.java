package io.github.anycollect.metric.prepared;

import io.github.anycollect.metric.Metric;

public interface PreparedMetric {
    Metric compile(long timestamp, double value);

    Metric compile(long timestamp, double... values);
}
