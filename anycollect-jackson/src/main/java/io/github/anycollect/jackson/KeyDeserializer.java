package io.github.anycollect.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.anycollect.metric.Key;

import java.io.IOException;

public final class KeyDeserializer extends StdDeserializer<Key> {
    protected KeyDeserializer() {
        super(Key.class);
    }

    @Override
    public Key deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext)
            throws IOException {
        String value = jsonParser.readValueAs(String.class);
        return Key.of(value);
    }
}
