package io.github.anycollect.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.anycollect.metric.Tags;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public final class TagsDeserializer extends StdDeserializer<Tags> {
    protected TagsDeserializer() {
        super(Tags.class);
    }

    @Override
    public Tags deserialize(final JsonParser parser, final DeserializationContext ctx) throws IOException {
        JsonNode root = parser.getCodec().readTree(parser);
        Iterator<Map.Entry<String, JsonNode>> fields = root.fields();
        Tags.Builder builder = Tags.builder();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> next = fields.next();
            String key = next.getKey();
            JsonNode node = next.getValue();
            String value = node.asText();
            builder.tag(key, value);
        }
        return builder.build();
    }
}
