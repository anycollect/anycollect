package io.github.anycollect.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.anycollect.metric.Tag;
import io.github.anycollect.metric.Tags;

import java.io.IOException;

public final class TagsSerializer extends StdSerializer<Tags> {
    public TagsSerializer() {
        super(Tags.class);
    }

    @Override
    public void serialize(final Tags value, final JsonGenerator gen, final SerializerProvider provider)
            throws IOException {
        gen.writeStartObject();
        for (Tag tag : value) {
            gen.writeStringField(tag.getKey(), tag.getValue());
        }
        gen.writeEndObject();
    }
}
