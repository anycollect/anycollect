package io.github.anycollect.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.anycollect.metric.Key;

import java.io.IOException;

public final class KeySerializer extends StdSerializer<Key> {
    protected KeySerializer() {
        super(Key.class);
    }

    @Override
    public void serialize(final Key key, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(key.normalize());
    }
}
