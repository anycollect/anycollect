package io.github.anycollect.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.anycollect.metric.Metric;

import java.io.IOException;

public final class MetricSerializer extends StdSerializer<Metric> {
    public MetricSerializer() {
        super(Metric.class);
    }

    @Override
    public void serialize(final Metric value, final JsonGenerator gen, final SerializerProvider provider)
            throws IOException {
        gen.writeStartObject();
        gen.writeStringField("key", value.getKey());
        gen.writeObjectField("tags", value.getTags());
        gen.writeObjectField("meta", value.getMeta());
        gen.writeNumberField("timestamp", value.getTimestamp());
        gen.writeEndObject();
    }
}
