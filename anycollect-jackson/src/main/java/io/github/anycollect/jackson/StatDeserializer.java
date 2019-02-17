package io.github.anycollect.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.anycollect.metric.Stat;

import java.io.IOException;

public class StatDeserializer extends StdDeserializer<Stat> {
    public StatDeserializer() {
        super(Stat.class);
    }

    @Override
    public Stat deserialize(final JsonParser parser, final DeserializationContext ctx) throws IOException {
        JsonNode root = parser.getCodec().readTree(parser);
        return Stat.parse(root.asText());
    }
}
