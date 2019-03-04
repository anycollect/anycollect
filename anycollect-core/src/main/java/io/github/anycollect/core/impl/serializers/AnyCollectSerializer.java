package io.github.anycollect.core.impl.serializers;

import io.github.anycollect.core.api.Serializer;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.metric.Measurement;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Tags;

import javax.annotation.Nonnull;
import java.util.List;

import static java.util.stream.Collectors.joining;

@Extension(name = AnyCollectSerializer.NAME, point = Serializer.class)
public final class AnyCollectSerializer implements Serializer {
    public static final String NAME = "AnyCollectSerializer";

    @ExtCreator
    public AnyCollectSerializer() {
    }

    @Nonnull
    @Override
    public String serialize(@Nonnull final Metric family) {
        String key = family.getKey();
        Tags tags = family.getTags();
        Tags meta = family.getMeta();
        List<? extends Measurement> measurements = family.getMeasurements();
        return key + "; "
                + (!tags.isEmpty() ? tags + "; " : "{}; ")
                + (!meta.isEmpty() ? meta + "; " : "{}; ")
                + measurements.stream()
                .map(this::serialize)
                .collect(joining(","));
    }

    private String serialize(final Measurement measurement) {
        return measurement.getStat() + "[" + measurement.getType() + "]"
                + "=" + measurement.getValue() + "(" + measurement.getUnit() + ")";
    }
}
