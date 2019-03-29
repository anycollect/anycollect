package io.github.anycollect.metric.frame;

import io.github.anycollect.metric.Tags;

import javax.annotation.Nonnull;

public interface MetricFrame {
    @Nonnull
    String getKey();

    @Nonnull
    Tags getTags();

    @Nonnull
    Tags getMeta();
}
