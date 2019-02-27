package io.github.anycollect.core.impl.serializers.graphite;

import io.github.anycollect.core.api.Serializer;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.metric.Measurement;
import io.github.anycollect.metric.MetricFamily;
import io.github.anycollect.metric.Tag;
import io.github.anycollect.metric.Tags;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

@Extension(name = TagGraphiteSerializer.NAME, point = Serializer.class)
public final class TagGraphiteSerializer implements Serializer {
    public static final String NAME = "TagGraphiteSerializer";

    @ExtCreator
    public TagGraphiteSerializer() {
    }

    @Nonnull
    @Override
    public String serialize(@Nonnull final MetricFamily family) {
        Tags tags = family.getTags();
        String key = family.getKey();
        long timestamp = TimeUnit.MILLISECONDS.toSeconds(family.getTimestamp());
        StringBuilder data = new StringBuilder();
        for (Measurement measurement : family.getMeasurements()) {
            if (Double.isNaN(measurement.getValue())) {
                continue;
            }
            data.append(key);
            data.append(".").append(measurement.getStat());
            if (!tags.isEmpty()) {
                for (Tag tag : tags) {
                    data.append(";")
                            .append(tag.getKey())
                            .append("=")
                            .append(tag.getValue());
                }
            }
            data.append(" ");
            data.append(measurement.getValue());
            data.append(" ");
            data.append(timestamp);
            data.append("\n");
        }
        return data.toString();
    }
}