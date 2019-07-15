package io.github.anycollect.core.api;

import io.github.anycollect.core.exceptions.SerialisationException;
import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CoderResult;

public interface Serializer {
    @Nonnull
    default String serialize(@Nonnull Metric metric) throws SerialisationException {
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        serialize(metric, buffer);
        buffer.flip();
        try {
            CharBuffer decode = Charset.forName("UTF-8").newDecoder().decode(buffer);
            return new String(decode.array(), 0, decode.limit());
        } catch (CharacterCodingException e) {
            throw new SerialisationException("could not serialize metric", e);
        }
    }

    CoderResult serialize(@Nonnull Metric metric, @Nonnull ByteBuffer buffer) throws SerialisationException;
}
