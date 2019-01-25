package io.github.anycollect.core.api.reader;

import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import java.util.List;

public interface JitReader extends Lifecycle {
    @Nonnull
    List<Metric> read();
}
