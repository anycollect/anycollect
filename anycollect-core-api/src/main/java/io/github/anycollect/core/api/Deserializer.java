package io.github.anycollect.core.api;

import io.github.anycollect.metric.MetricFamily;

import javax.annotation.Nonnull;

public interface Deserializer {
    @Nonnull
    MetricFamily deserialize(@Nonnull String source);
}
