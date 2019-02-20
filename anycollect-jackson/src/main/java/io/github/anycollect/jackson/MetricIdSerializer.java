package io.github.anycollect.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.anycollect.metric.MetricId;

import java.io.IOException;

public final class MetricIdSerializer extends StdSerializer<MetricId> {
    protected MetricIdSerializer() {
        super(MetricId.class);
    }

    @Override
    public void serialize(final MetricId value, final JsonGenerator gen, final SerializerProvider provider)
            throws IOException {
        gen.writeStartObject();
        gen.writeStringField("what", value.getKey());
        gen.writeStringField("unit", value.getUnit());
        gen.writeObjectField("stat", value.getStat());
        gen.writeObjectField("mtype", value.getType());
        gen.writeObjectField("tags", value.getTags());
        gen.writeObjectField("meta", value.getMetaTags());
        gen.writeEndObject();
    }
}
