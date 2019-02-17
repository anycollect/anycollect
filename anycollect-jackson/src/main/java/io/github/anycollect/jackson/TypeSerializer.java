package io.github.anycollect.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.anycollect.metric.Type;

import java.io.IOException;

public class TypeSerializer extends StdSerializer<Type> {
    public TypeSerializer() {
        super(Type.class);
    }

    @Override
    public void serialize(final Type type, final JsonGenerator gen, final SerializerProvider provider)
            throws IOException {
        gen.writeStartObject();
        gen.writeStringField("id", type.getTagValue());
        gen.writeEndObject();
    }
}
