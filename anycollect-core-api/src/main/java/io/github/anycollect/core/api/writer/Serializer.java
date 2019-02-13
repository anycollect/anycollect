package io.github.anycollect.core.api.writer;

import io.github.anycollect.core.api.common.Plugin;
import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import java.io.OutputStream;
import java.util.List;

public interface Serializer extends Plugin {
    void serialize(@Nonnull Metric metric, @Nonnull OutputStream out);

    void serialize(@Nonnull List<Metric> metrics, @Nonnull OutputStream out);
}