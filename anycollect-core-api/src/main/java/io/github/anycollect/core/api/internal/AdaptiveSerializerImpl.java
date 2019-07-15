package io.github.anycollect.core.api.internal;

import io.github.anycollect.core.api.Serializer;
import io.github.anycollect.core.exceptions.SerialisationException;
import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.nio.charset.CoderResult;

public final class AdaptiveSerializerImpl implements AdaptiveSerializer {
    private static final int DEFAULT_INIT_BUFFER_SIZE = 1024;
    private static final int DEFAULT_MAX_BUFFER_SIZE = 16 * DEFAULT_INIT_BUFFER_SIZE;
    private final Serializer serializer;
    private ByteBuffer buffer;
    private final int maxBufferSize;

    public AdaptiveSerializerImpl(@Nonnull final Serializer serializer) {
        this(serializer, DEFAULT_INIT_BUFFER_SIZE, DEFAULT_MAX_BUFFER_SIZE);
    }

    public AdaptiveSerializerImpl(@Nonnull final Serializer serializer,
                                  final int initBufferSize,
                                  final int maxBufferSize) {
        this.buffer = ByteBuffer.allocate(initBufferSize);
        this.serializer = serializer;
        this.maxBufferSize = maxBufferSize;
    }

    @Override
    public ByteBuffer serialize(@Nonnull final Metric metric) throws SerialisationException {
        serializer.serialize(metric, buffer);

        if (buffer == null) {
            throw new IllegalStateException("buffer has not been released yet");
        }

        CoderResult coderResult;
        do {
            buffer.clear();
            coderResult = serializer.serialize(metric, buffer);
            buffer.flip();
            if (coderResult.isOverflow()) {
                if (buffer.capacity() == maxBufferSize) {
                    throw new SerialisationException("overflow using maximal allowed buffer size");
                }
                int newCapacity = buffer.capacity() * 2;
                if (newCapacity >= maxBufferSize) {
                    newCapacity = maxBufferSize;
                }
                buffer = ByteBuffer.allocate(newCapacity);
            }
        } while (coderResult.isOverflow());
        if (coderResult.isError()) {
            throw new SerialisationException("fail to serialize metric " + coderResult);
        }
        ByteBuffer buffer = this.buffer;
        this.buffer = null;

        return buffer;
    }

    @Override
    public void release(@Nonnull final ByteBuffer buffer) {
        this.buffer = buffer;
    }
}
