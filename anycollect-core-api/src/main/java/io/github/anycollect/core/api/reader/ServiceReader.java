package io.github.anycollect.core.api.reader;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.common.Lifecycle;

import javax.annotation.Nonnull;

public interface ServiceReader extends Lifecycle {
    void start(@Nonnull Dispatcher dispatcher);
}
