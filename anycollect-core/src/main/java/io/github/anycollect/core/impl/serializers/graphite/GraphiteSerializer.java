package io.github.anycollect.core.impl.serializers.graphite;

import io.github.anycollect.core.api.Serializer;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.metric.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

@Extension(name = GraphiteSerializer.NAME, point = Serializer.class)
public final class GraphiteSerializer implements Serializer {
    public static final String NAME = "GraphiteSerializer";
    private final GraphiteSerializerConfig config;

    @ExtCreator
    public GraphiteSerializer(@ExtConfig(optional = true) @Nullable final GraphiteSerializerConfig optConfig) {
        this.config = optConfig != null ? optConfig : GraphiteSerializerConfig.DEFAULT;
    }

    @Override
    public void serialize(@Nonnull final Metric metric, @Nonnull final StringBuilder builder) {
        Tags tags = config.tags().concat(metric.getTags());
        String key = metric.getKey();
        long timestamp = TimeUnit.MILLISECONDS.toSeconds(metric.getTimestamp());
        builder.setLength(0);
        for (Measurement measurement : metric.getMeasurements()) {
            builder.append(config.prefix());
            if (Double.isNaN(measurement.getValue())) {
                continue;
            }
            builder.append(key);
            if (!measurement.getUnit().isEmpty()) {
                builder.append(".").append(measurement.getUnit());
            }
            if (measurement.getStat().equals(Stat.value())) {
                if (measurement.getType() == Type.GAUGE) {
                    builder.append(".").append("gauge");
                } else if (measurement.getType() == Type.COUNTER) {
                    builder.append(".").append("counter");
                }
            } else {
                builder.append(".").append(measurement.getStat());
            }
            if (!tags.isEmpty()) {
                if (!config.tagSupport()) {
                    for (Tag tag : tags) {
                        builder.append(".");
                        normalize(tag.getKey(), builder);
                        builder.append(".");
                        normalize(tag.getValue(), builder);
                    }
                } else {
                    for (Tag tag : tags) {
                        builder.append(";");
                        sanitize(tag.getKey(), builder);
                        builder.append("=");
                        sanitize(tag.getValue(), builder);
                    }
                }
            }
            builder.append(" ");
            builder.append(measurement.getValue());
            builder.append(" ");
            builder.append(timestamp);
            builder.append("\n");
        }
    }

    private void normalize(final String source, final StringBuilder builder) {
        boolean upper = false;
        for (int i = 0; i < source.length(); ++i) {
            char ch = source.charAt(i);
            if (ch == '.') {
                upper = true;
            }
            if (ch == ' ') {
                builder.append('_');
            } else {
                if (upper) {
                    builder.append(Character.toUpperCase(ch));
                } else {
                    builder.append(ch);
                }
            }
        }
    }

    private void sanitize(final String source, final StringBuilder builder) {
        for (int i = 0; i < source.length(); ++i) {
            char ch = source.charAt(i);
            if (ch == ' ') {
                builder.append('_');
            } else {
                builder.append(ch);
            }
        }
    }
}
