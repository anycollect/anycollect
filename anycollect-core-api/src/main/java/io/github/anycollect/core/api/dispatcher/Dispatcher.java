package io.github.anycollect.core.api.dispatcher;

import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import java.util.List;

public interface Dispatcher {
    void dispatch(@Nonnull Metric metric);

    void dispatch(@Nonnull List<Metric> metrics);
}
