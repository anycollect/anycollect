package io.github.anycollect.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.anycollect.metric.Point;

import java.io.IOException;

public final class MetricSerializer extends StdSerializer<Point> {
    public MetricSerializer() {
        super(Point.class);
    }

    @Override
    public void serialize(final Point value, final JsonGenerator gen, final SerializerProvider provider)
            throws IOException {
        gen.writeStartObject();
        gen.writeObjectField("id", value.getId());
        gen.writeNumberField("value", value.getValue());
        gen.writeNumberField("timestamp", value.getTimestamp());
        gen.writeEndObject();
    }
}
