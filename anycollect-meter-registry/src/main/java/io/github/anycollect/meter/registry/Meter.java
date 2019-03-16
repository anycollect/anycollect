package io.github.anycollect.meter.registry;

import io.github.anycollect.metric.MeterId;
import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import java.util.List;

public interface Meter {
    @Nonnull
    MeterId getId();

    List<Metric> measure();
}
