package io.github.anycollect.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.anycollect.metric.Stat;

import java.io.IOException;

public class StatSerializer extends StdSerializer<Stat> {
    public StatSerializer() {
        super(Stat.class);
    }

    @Override
    public void serialize(final Stat value, final JsonGenerator gen, final SerializerProvider provider)
            throws IOException {
        gen.writeStartObject();
        gen.writeStringField("id", value.getTagValue());
        gen.writeEndObject();
    }
}
