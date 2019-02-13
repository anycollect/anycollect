package io.github.anycollect.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.anycollect.metric.MetricId;
import io.github.anycollect.metric.Tags;

import java.io.IOException;

public final class MetricIdDeserializer extends StdDeserializer<MetricId> {
    public MetricIdDeserializer() {
        super(MetricId.class);
    }

    @Override
    public MetricId deserialize(final JsonParser parser, final DeserializationContext ctxt) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        Tags tags = serializeTags(node.get("tags"), parser.getCodec(), ctxt);
        Tags meta = serializeTags(node.get("meta"), parser.getCodec(), ctxt);
        return MetricId.builder()
                .concatTags(tags)
                .concatMeta(meta)
                .build();
    }

    private Tags serializeTags(final TreeNode node, final ObjectCodec codec, final DeserializationContext ctx)
            throws IOException {
        if (node == null) {
            return Tags.empty();
        }
        return ctx.readValue(node.traverse(codec), Tags.class);
    }
}
