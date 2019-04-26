package io.github.anycollect.core.impl.serializers.graphite;

import com.google.common.base.CaseFormat;
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
                        builder.append(".")
                                .append(normalize(sanitize(tag.getKey())))
                                .append(".")
                                .append(normalize(sanitize(tag.getValue())));
                    }
                } else {
                    for (Tag tag : tags) {
                        builder.append(";")
                                .append(normalize(sanitize(tag.getKey())))
                                .append("=")
                                .append(normalize(sanitize(tag.getValue())));
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

    // TODO tune
    private String normalize(final String source) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, source.replace('.', '_'));
    }

    private String sanitize(final String source) {
        return FORBIDDEN.matcher(source).replaceAll("_");
    }
}
