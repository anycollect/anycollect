package io.github.anycollect.core.api;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.extensions.annotations.ExtPoint;

import javax.annotation.Nonnull;

@ExtPoint
public interface Reader {
    void start(@Nonnull Dispatcher dispatcher);
}
