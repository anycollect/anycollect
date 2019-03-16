package io.github.anycollect.core.api;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import java.util.List;

public interface Processor extends Route {
    void start(@Nonnull Dispatcher dispatcher);

    void submit(@Nonnull List<? extends Metric> sources);
}
