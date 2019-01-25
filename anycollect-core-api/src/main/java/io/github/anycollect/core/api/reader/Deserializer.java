package io.github.anycollect.core.api.reader;

import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import java.io.InputStream;

public interface Deserializer {
    @Nonnull
    Metric deserialize(@Nonnull InputStream in);
}
