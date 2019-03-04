package io.github.anycollect.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.anycollect.metric.ImmutablePoint;
import io.github.anycollect.metric.Point;
import io.github.anycollect.metric.PointId;

import java.io.IOException;

public final class MetricDeserializer extends StdDeserializer<Point> {
    public MetricDeserializer() {
        super(Point.class);
    }

    @Override
    public Point deserialize(final JsonParser parser, final DeserializationContext ctx) throws IOException {
        JsonNode root = parser.getCodec().readTree(parser);

        PointId id = ctx.readValue(root.get("id").traverse(parser.getCodec()), PointId.class);
        double value = root.get("value").doubleValue();
        long timestamp = root.get("timestamp").longValue();
        return new ImmutablePoint(id, value, timestamp);
    }
}
