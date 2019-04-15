package io.github.anycollect.core.api.serialization;

import io.github.anycollect.core.api.Serializer;
import io.github.anycollect.core.exceptions.SerialisationException;
import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
final class StringBuilderRoundRobinSerializer implements RoundRobinSerializer<StringBuilder> {
    private final Serializer serializer;

    StringBuilderRoundRobinSerializer(@Nonnull final Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public void serialize(@Nonnull final Metric metric, @Nonnull final StringBuilder carrier)
            throws SerialisationException {
        serializer.serialize(metric, carrier);
    }
}
