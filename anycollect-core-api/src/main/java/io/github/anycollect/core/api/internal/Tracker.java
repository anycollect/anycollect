package io.github.anycollect.core.api.internal;

import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;

public interface Tracker {
    void drop(@Nonnull Metric metric);

    void accept(@Nonnull Metric metric);
}
