package io.github.anycollect.metric.prepared;

import io.github.anycollect.metric.MetricFamily;

public interface PreparedMetricFamily {
    MetricFamily compile(long timestamp, double value);

    MetricFamily compile(long timestamp, double... values);
}
