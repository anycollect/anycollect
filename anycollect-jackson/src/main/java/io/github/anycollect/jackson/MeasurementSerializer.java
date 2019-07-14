package io.github.anycollect.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.anycollect.metric.Measurement;

import java.io.IOException;

public final class MeasurementSerializer extends StdSerializer<Measurement> {
    protected MeasurementSerializer() {
        super(Measurement.class);
    }

    @Override
    public void serialize(final Measurement value, final JsonGenerator gen, final SerializerProvider provider)
            throws IOException {
        gen.writeStartObject();
        gen.writeStringField("unit", value.getUnit());
        gen.writeObjectField("stat", value.getStat());
        gen.writeObjectField("mtype", value.getType());
        gen.writeObjectField("value", value.getValue());
        gen.writeEndObject();
    }
}
