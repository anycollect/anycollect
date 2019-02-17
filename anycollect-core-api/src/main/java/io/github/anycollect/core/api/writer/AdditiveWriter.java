package io.github.anycollect.core.api.writer;

import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.core.api.common.Plugin;
import io.github.anycollect.metric.MetricFamily;

import javax.annotation.Nonnull;
import java.util.List;

public interface AdditiveWriter extends Plugin, Lifecycle {
    void add(@Nonnull MetricFamily family);

    void add(@Nonnull List<MetricFamily> families);
}
