package io.github.anycollect.core.api.serialization;

import io.github.anycollect.core.api.Serializer;
import io.github.anycollect.core.exceptions.SerialisationException;
import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.nio.ByteBuffer;

@NotThreadSafe
public interface RoundRobinSerializer<T> {
    void serialize(@Nonnull Metric metric, @Nonnull T carrier) throws SerialisationException;

    static RoundRobinSerializer<StringBuilder> toStringBuilder(Serializer serializer) {
        return new StringBuilderRoundRobinSerializer(serializer);
    }

    static RoundRobinSerializer<ByteBuffer> toByteBuffer(Serializer serializer) {
        return new ByteBufferRoundRobinSerializer(serializer);
    }
}
