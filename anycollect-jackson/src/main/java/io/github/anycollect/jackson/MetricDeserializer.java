package io.github.anycollect.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.github.anycollect.metric.Measurement;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MetricDeserializer extends StdDeserializer<Metric> {
    public MetricDeserializer() {
        super(Metric.class);
    }

    @Override
    public Metric deserialize(final JsonParser parser, final DeserializationContext ctx) throws IOException {
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);
        String key = node.get("key").asText();
        Tags tags = deserializeTags(node.get("tags"), codec, ctx);
        Tags meta = deserializeTags(node.get("meta"), codec, ctx);
        long timestamp = node.get("timestamp").longValue();
        List<Measurement> measurements = deserializeMeasurements((ArrayNode) node.get("measurements"), codec, ctx);
        return Metric.builder()
                .key(key)
                .concatTags(tags)
                .concatMeta(meta)
                .measurements(measurements)
                .at(timestamp)
                .build();
    }

    private List<Measurement> deserializeMeasurements(final ArrayNode node,
                                                      final ObjectCodec codec,
                                                      final DeserializationContext ctx)
            throws IOException {
        if (node == null) {
            return Collections.emptyList();
        }
        List<Measurement> measurements = new ArrayList<>();
        for (int i = 0; i < node.size(); ++i) {
            measurements.add(deserializeMeasurement(node.get(i), codec, ctx));
        }
        return measurements;
    }

    private Measurement deserializeMeasurement(final TreeNode node,
                                               final ObjectCodec codec,
                                               final DeserializationContext ctx)
            throws IOException {
        return ctx.readValue(node.traverse(codec), Measurement.class);
    }

    private Tags deserializeTags(final TreeNode node, final ObjectCodec codec, final DeserializationContext ctx)
            throws IOException {
        if (node == null) {
            return Tags.empty();
        }
        return ctx.readValue(node.traverse(codec), Tags.class);
    }
}
