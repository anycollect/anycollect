package io.github.anycollect.core.api;

import io.github.anycollect.extensions.annotations.ExtPoint;
import io.github.anycollect.metric.MetricFamily;

import javax.annotation.Nonnull;

@ExtPoint
public interface Serializer {
    @Nonnull
    String serialize(@Nonnull MetricFamily family);
}