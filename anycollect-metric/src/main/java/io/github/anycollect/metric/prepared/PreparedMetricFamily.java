package io.github.anycollect.metric.prepared;

import io.github.anycollect.metric.Metric;

public interface PreparedMetricFamily {
    Metric compile(long timestamp, double value);

    Metric compile(long timestamp, double... values);
}
