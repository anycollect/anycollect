package io.github.anycollect.micrometer;

import io.github.anycollect.metric.Meter;

public interface MeterAdapter {
    Meter getMeter();
}
