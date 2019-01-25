package io.github.anycollect.core.api.internal;

import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;

public interface Tracker {
    void dropWrite(@Nonnull Metric metric);

    void acceptWrite(@Nonnull Metric metric);
}
