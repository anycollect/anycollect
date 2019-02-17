package io.github.anycollect.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.anycollect.metric.MetricId;
import io.github.anycollect.metric.Stat;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.metric.Type;

import java.io.IOException;

public final class MetricIdDeserializer extends StdDeserializer<MetricId> {
    public MetricIdDeserializer() {
        super(MetricId.class);
    }

    @Override
    public MetricId deserialize(final JsonParser parser, final DeserializationContext ctxt) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        String key = node.get("what").asText();
        String unit = node.get("unit").asText();
        Stat stat = Stat.parse(node.get("stat").asText());
        Type type = Type.parse(node.get("mtype").asText());
        Tags tags = serializeTags(node.get("tags"), parser.getCodec(), ctxt);
        Tags meta = serializeTags(node.get("meta"), parser.getCodec(), ctxt);
        return MetricId.key(key)
                .unit(unit)
                .stat(stat)
                .type(type)
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
