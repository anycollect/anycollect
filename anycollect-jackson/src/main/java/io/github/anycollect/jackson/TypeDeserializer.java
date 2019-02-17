package io.github.anycollect.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.anycollect.metric.Type;

import java.io.IOException;

public class TypeDeserializer extends StdDeserializer<Type> {
    public TypeDeserializer() {
        super(Type.class);
    }

    @Override
    public Type deserialize(final JsonParser parser, final DeserializationContext ctx) throws IOException {
        JsonNode root = parser.getCodec().readTree(parser);
        return Type.parse(root.asText());
    }
}
