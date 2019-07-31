package io.github.anycollect.core.impl.serializers;

import io.github.anycollect.core.api.Serializer;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.metric.Sample;
import io.github.anycollect.metric.Tag;
import io.github.anycollect.metric.Tags;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.Iterator;

@Extension(name = AnyCollectSerializer.NAME, point = Serializer.class)
public final class AnyCollectSerializer implements Serializer {
    public static final String NAME = "AnyCollectSerializer";
    private final StringBuilder builder = new StringBuilder(1024);
    private final CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();

    @ExtCreator
    public AnyCollectSerializer() {
    }

    @Override
    public CoderResult serialize(@Nonnull final Sample sample, @Nonnull final ByteBuffer buffer) {
        builder.setLength(0);
        builder.append(sample.getKey()).append(";");
        serialize(sample.getTags(), builder);
        builder.append(";");
        serialize(sample.getMeta(), builder);
        builder.append(";");
        builder.append(sample.getStat())
                .append("[")
                .append(sample.getType())
                .append("]")
                .append("=")
                .append(sample.getValue())
                .append("(")
                .append(sample.getUnit())
                .append(")");
        return encoder.encode(CharBuffer.wrap(builder), buffer, true);
    }

    private void serialize(final Tags tags, final StringBuilder builder) {
        if (tags == null) {
            builder.append("{}");
            return;
        }
        builder.append("{");
        Iterator<Tag> iterator = tags.iterator();
        while (iterator.hasNext()) {
            Tag tag = iterator.next();
            builder.append(tag.getKey()).append("=").append("\"").append(tag.getValue()).append("\"");
            if (iterator.hasNext()) {
                builder.append(",");
            }
        }
        builder.append("}");
    }
}
