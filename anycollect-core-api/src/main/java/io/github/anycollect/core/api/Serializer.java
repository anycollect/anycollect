package io.github.anycollect.core.api;

import io.github.anycollect.core.exceptions.SerialisationException;
import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;

public interface Serializer {
    @Nonnull
    String serialize(@Nonnull Metric metric) throws SerialisationException;
}
