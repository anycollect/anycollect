package io.github.anycollect.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.anycollect.metric.*;

import java.io.IOException;

public final class MeasurementDeserializer extends StdDeserializer<Measurement> {
    public MeasurementDeserializer() {
        super(Measurement.class);
    }

    @Override
    public Measurement deserialize(final JsonParser parser, final DeserializationContext ctx) throws IOException {
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);
        String unit = node.get("unit").asText();
        Stat stat = node.get("stat").traverse(codec).readValueAs(Stat.class);
        Type type = node.get("mtype").traverse(codec).readValueAs(Type.class);
        double value = node.get("value").asDouble();
        return new ImmutableMeasurement(stat, type, unit, value);
    }
}
