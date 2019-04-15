package io.github.anycollect.core.api;

import io.github.anycollect.core.exceptions.SerialisationException;
import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;

public interface Serializer {
    @Nonnull
    default String serialize(@Nonnull Metric metric) throws SerialisationException {
        StringBuilder builder = new StringBuilder();
        serialize(metric, builder);
        return builder.toString();
    }

    void serialize(@Nonnull Metric metric, @Nonnull StringBuilder builder) throws SerialisationException;
}
