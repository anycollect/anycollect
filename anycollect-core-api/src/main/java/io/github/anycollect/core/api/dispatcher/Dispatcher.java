package io.github.anycollect.core.api.dispatcher;

import io.github.anycollect.metric.MetricFamily;

import javax.annotation.Nonnull;
import java.util.List;

public interface Dispatcher {
    void dispatch(@Nonnull MetricFamily family);

    void dispatch(@Nonnull List<MetricFamily> families);
}
