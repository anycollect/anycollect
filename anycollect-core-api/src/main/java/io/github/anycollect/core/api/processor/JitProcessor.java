package io.github.anycollect.core.api.processor;

import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.core.api.common.Plugin;
import io.github.anycollect.metric.MetricFamily;
import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import java.util.List;

public interface JitProcessor extends Plugin, Lifecycle {
    @Nonnull
    List<Metric> process(@Nonnull List<MetricFamily> sources);

    @Nonnull
    Metric process(@Nonnull MetricFamily source);
}
