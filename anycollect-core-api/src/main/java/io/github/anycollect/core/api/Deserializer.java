package io.github.anycollect.core.api;

import io.github.anycollect.extensions.annotations.ExtPoint;
import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;

@ExtPoint
public interface Deserializer {
    @Nonnull
    Metric deserialize(@Nonnull String source);
}
