package io.github.anycollect.meter.registry;

import io.github.anycollect.metric.Meter;
import io.github.anycollect.metric.MetricFamily;

import java.util.List;

public interface Measurable extends Meter {
    List<MetricFamily> measure();
}
