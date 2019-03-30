package io.github.anycollect.metric.frame;

import javax.annotation.Nonnull;

public interface Reframer {
    @Nonnull
    MetricFrame reframe(@Nonnull MetricFrame source);
}
