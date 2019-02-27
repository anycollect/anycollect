package io.github.anycollect.core.api;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.metric.MetricFamily;

import javax.annotation.Nonnull;
import java.util.List;

public interface Processor {
    void start(@Nonnull Dispatcher dispatcher);

    void submit(@Nonnull MetricFamily source);

    void submit(@Nonnull List<MetricFamily> sources);
}
