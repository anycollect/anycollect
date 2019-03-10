package io.github.anycollect.deserializers.dummy;

import io.github.anycollect.core.api.Deserializer;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.metric.Measurement;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Tags;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

@Extension(name = DummyDeserializer.NAME, point = Deserializer.class)
public final class DummyDeserializer implements Deserializer {
    public static final String NAME = "DummyDeserializer";

    @ExtCreator
    public DummyDeserializer() {
    }

    @Nonnull
    @Override
    public Metric deserialize(@Nonnull final String source) {
        String[] parts = source.split(" ");
        String path = parts[0];
        double value = Double.parseDouble(parts[1]);
        long timestamp = TimeUnit.SECONDS.toMillis(Long.parseLong(parts[2]));
        Measurement unknowns = Measurement.gauge(value, "unknowns");
        return Metric.of(path, Tags.empty(), Tags.empty(), unknowns, timestamp);
    }
}
