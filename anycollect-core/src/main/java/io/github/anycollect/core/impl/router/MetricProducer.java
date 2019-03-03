package io.github.anycollect.core.impl.router;

import io.github.anycollect.core.api.dispatcher.Dispatcher;

import javax.annotation.Nonnull;

public interface MetricProducer extends RouterNode {
    void start(@Nonnull Dispatcher dispatcher);
}
