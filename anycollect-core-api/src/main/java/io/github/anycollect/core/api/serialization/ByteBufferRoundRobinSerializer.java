package io.github.anycollect.core.api.serialization;

import io.github.anycollect.core.api.Serializer;
import io.github.anycollect.core.exceptions.SerialisationException;
import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.nio.ByteBuffer;

@NotThreadSafe
final class ByteBufferRoundRobinSerializer implements RoundRobinSerializer<ByteBuffer> {
    private final StringBuilder builder = new StringBuilder();
    private final Serializer serializer;
    private char[] buffer;

    ByteBufferRoundRobinSerializer(@Nonnull final Serializer serializer) {
        this.serializer = serializer;
        this.buffer = new char[1024];
    }

    @Override
    public void serialize(@Nonnull final Metric metric, @Nonnull final ByteBuffer carrier)
            throws SerialisationException {
        serializer.serialize(metric, builder);
        if (builder.length() > this.builder.length()) {
            this.buffer = new char[buffer.length];
        }
        builder.getChars(0, builder.length(), this.buffer, 0);
        carrier.clear();
        carrier.asCharBuffer().put(this.buffer, 0, builder.length());
    }
}
