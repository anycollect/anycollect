package io.github.anycollect.core.api.processor;

import io.github.anycollect.core.api.common.Plugin;
import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import java.util.List;

public interface ServiceProcessor extends Plugin, Lifecycle {
    void start(@Nonnull Dispatcher dispatcher);

    void submit(@Nonnull Metric source);

    void submit(@Nonnull List<Metric> sources);
}
