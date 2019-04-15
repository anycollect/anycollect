package io.github.anycollect.core.impl.serializers;

import io.github.anycollect.core.api.Serializer;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.metric.Measurement;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Tag;
import io.github.anycollect.metric.Tags;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;

@Extension(name = AnyCollectSerializer.NAME, point = Serializer.class)
public final class AnyCollectSerializer implements Serializer {
    public static final String NAME = "AnyCollectSerializer";

    @ExtCreator
    public AnyCollectSerializer() {
    }

    @Override
    public void serialize(@Nonnull final Metric metric, @Nonnull final StringBuilder builder) {
        builder.setLength(0);
        builder.append(metric.getKey()).append(";");
        serialize(metric.getTags(), builder);
        builder.append(";");
        serialize(metric.getMeta(), builder);
        builder.append(";");
        serialize(metric.getMeasurements(), builder);
    }

    private void serialize(final List<? extends Measurement> measurements, final StringBuilder builder) {
        Iterator<? extends Measurement> iterator = measurements.iterator();
        while (iterator.hasNext()) {
            Measurement measurement = iterator.next();
            serialize(measurement, builder);
            if (iterator.hasNext()) {
                builder.append(",");
            }
        }
    }

    private void serialize(final Measurement measurement, final StringBuilder builder) {
        builder.append(measurement.getStat())
                .append("[")
                .append(measurement.getType())
                .append("]")
                .append("=")
                .append(measurement.getValue())
                .append("(")
                .append(measurement.getUnit())
                .append(")");
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
