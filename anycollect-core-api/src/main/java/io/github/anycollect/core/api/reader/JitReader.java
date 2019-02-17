package io.github.anycollect.core.api.reader;

import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.core.api.common.Plugin;
import io.github.anycollect.metric.MetricFamily;

import javax.annotation.Nonnull;
import java.util.List;

public interface JitReader extends Plugin, Lifecycle {
    @Nonnull
    List<MetricFamily> read();
}
