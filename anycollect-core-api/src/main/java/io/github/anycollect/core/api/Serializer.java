package io.github.anycollect.core.api;

import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;

public interface Serializer {
    @Nonnull
    String serialize(@Nonnull Metric source);
}
