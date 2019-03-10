package io.github.anycollect.core.api;

import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;

public interface Deserializer {
    @Nonnull
    Metric deserialize(@Nonnull String string);
}
