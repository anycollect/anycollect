package io.github.anycollect.core.api.reader;

import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.core.api.common.Plugin;
import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.extensions.annotations.ExtPoint;

import javax.annotation.Nonnull;

@ExtPoint
public interface ServiceReader extends Plugin, Lifecycle {
    void start(@Nonnull Dispatcher dispatcher);
}
