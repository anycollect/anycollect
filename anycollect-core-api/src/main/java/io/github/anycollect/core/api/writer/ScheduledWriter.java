package io.github.anycollect.core.api.writer;

import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import java.util.List;

public interface ScheduledWriter extends Lifecycle {
    void write(@Nonnull Metric metric);

    void write(@Nonnull List<Metric> metrics);
}
