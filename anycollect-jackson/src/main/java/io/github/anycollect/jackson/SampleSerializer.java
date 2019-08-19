package io.github.anycollect.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.anycollect.metric.Sample;
import io.github.anycollect.metric.Metric;

import java.io.IOException;

public final class SampleSerializer extends StdSerializer<Sample> {
    public SampleSerializer() {
        super(Sample.class);
    }

    @Override
    public void serialize(final Sample value, final JsonGenerator gen, final SerializerProvider provider)
            throws IOException {
        gen.writeStartObject();
        Metric id = value.getMetric();
        gen.writeObjectField("key", id.getKey());
        gen.writeObjectField("tags", id.getTags());
        gen.writeObjectField("meta", id.getMeta());
        gen.writeObjectField("stat", id.getStat());
        gen.writeObjectField("unit", id.getUnit());
        gen.writeNumberField("value", value.getValue());
        gen.writeNumberField("timestamp", value.getTimestamp());
        gen.writeEndObject();
    }
}
