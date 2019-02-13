package io.github.anycollect.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.anycollect.metric.ImmutableMetric;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.MetricId;

import java.io.IOException;

public final class MetricDeserializer extends StdDeserializer<Metric> {
    public MetricDeserializer() {
        super(Metric.class);
    }

    @Override
    public Metric deserialize(final JsonParser parser, final DeserializationContext ctx) throws IOException {
        JsonNode root = parser.getCodec().readTree(parser);

        MetricId id = ctx.readValue(root.get("id").traverse(parser.getCodec()), MetricId.class);
        double value = root.get("value").doubleValue();
        long timestamp = root.get("timestamp").longValue();
        return new ImmutableMetric(id, value, timestamp);
    }
}
