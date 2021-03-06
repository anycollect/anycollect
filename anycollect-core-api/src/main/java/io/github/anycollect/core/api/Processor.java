package io.github.anycollect.core.api;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.metric.Sample;

import javax.annotation.Nonnull;
import java.util.List;

public interface Processor extends Route {
    void start(@Nonnull Dispatcher dispatcher);

    void submit(@Nonnull List<? extends Sample> sources);
}
