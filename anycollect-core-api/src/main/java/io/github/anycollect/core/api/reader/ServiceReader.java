package io.github.anycollect.core.api.reader;

import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.core.api.common.Plugin;
import io.github.anycollect.core.api.dispatcher.Dispatcher;

import javax.annotation.Nonnull;

public interface ServiceReader extends Plugin, Lifecycle {
    void start(@Nonnull Dispatcher dispatcher);
}
