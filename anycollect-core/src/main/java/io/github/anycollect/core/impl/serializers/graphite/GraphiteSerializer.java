package io.github.anycollect.core.impl.serializers.graphite;

import io.github.anycollect.core.api.Serializer;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.metric.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Extension(name = GraphiteSerializer.NAME, point = Serializer.class)
public final class GraphiteSerializer implements Serializer {
    public static final String NAME = "GraphiteSerializer";
    private static final Pattern FORBIDDEN = Pattern.compile("\\s");
    private final GraphiteSerializerConfig config;

    @ExtCreator
    public GraphiteSerializer(@ExtConfig(optional = true) @Nullable final GraphiteSerializerConfig optConfig) {
        this.config = optConfig != null ? optConfig : GraphiteSerializerConfig.DEFAULT;
    }

    @Nonnull
    @Override
    public String serialize(@Nonnull final Metric metric) {
        Tags tags = metric.getTags();
        String key = metric.getKey();
        long timestamp = TimeUnit.MILLISECONDS.toSeconds(metric.getTimestamp());
        StringBuilder data = new StringBuilder();
        for (Measurement measurement : metric.getMeasurements()) {
            data.append(config.prefix());
            if (Double.isNaN(measurement.getValue())) {
                continue;
            }
            data.append(key);
            if (!measurement.getUnit().isEmpty()) {
                data.append(".").append(measurement.getUnit());
            }
            if (measurement.getStat().equals(Stat.value())) {
                if (measurement.getType() == Type.GAUGE) {
                    data.append(".").append("gauge");
                } else if (measurement.getType() == Type.COUNTER) {
                    data.append(".").append("counter");
                }
            } else {
                data.append(".").append(measurement.getStat());
            }
            if (!tags.isEmpty()) {
                if (!config.tagSupport()) {
                    for (Tag tag : tags) {
                        data.append(".")
                                .append(sanitize(tag.getKey()))
                                .append(".")
                                .append(sanitize(tag.getValue()));
                    }
                } else {
                    for (Tag tag : tags) {
                        data.append(";")
                                .append(sanitize(tag.getKey()))
                                .append("=")
                                .append(sanitize(tag.getValue()));
                    }
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

    private String sanitize(final String source) {
        return FORBIDDEN.matcher(source).replaceAll("_");
    }
}
