package io.github.anycollect.metric.prepared;

import io.github.anycollect.metric.Measurement;

public interface PreparedMeasurement {
    Measurement compile(double value);
}
