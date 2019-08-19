package io.github.anycollect.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.anycollect.metric.*;

import java.io.IOException;

public final class SampleDeserializer extends StdDeserializer<Sample> {
    public SampleDeserializer() {
        super(Sample.class);
    }

    @Override
    public Sample deserialize(final JsonParser parser, final DeserializationContext ctx) throws IOException {
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);
        Key key = node.get("key").traverse(codec).readValueAs(Key.class);
        Stat stat = node.get("stat").traverse(codec).readValueAs(Stat.class);
        Tags tags = node.has("tags") ? node.get("tags").traverse(codec).readValueAs(Tags.class) : Tags.empty();
        Tags meta = node.has("meta") ? node.get("meta").traverse(codec).readValueAs(Tags.class) : Tags.empty();
        String unit = node.has("unit") ? node.get("unit").asText() : "";
        long timestamp = node.get("timestamp").longValue();
        double value = node.get("value").asDouble();
        return Metric.builder()
                .key(key)
                .tags(tags)
                .meta(meta)
                .metric(stat, unit)
                .sample(value, timestamp);
    }
}
