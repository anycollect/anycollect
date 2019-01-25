package io.github.anycollect.core.api.processor;

import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import java.util.List;

public interface JitProcessor extends Lifecycle {
    @Nonnull
    List<Metric> process(@Nonnull List<Metric> sources);

    @Nonnull
    Metric process(@Nonnull Metric source);
}
