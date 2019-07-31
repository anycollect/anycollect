package io.github.anycollect.core.api.internal;

import io.github.anycollect.core.api.Serializer;
import io.github.anycollect.core.exceptions.SerialisationException;
import io.github.anycollect.metric.Sample;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;

public interface AdaptiveSerializer {
    static AdaptiveSerializer wrap(@Nonnull Serializer serializer) {
        return new AdaptiveSerializerImpl(serializer);
    }

    ByteBuffer serialize(@Nonnull Sample sample) throws SerialisationException;

    void release(@Nonnull ByteBuffer buffer);
}
