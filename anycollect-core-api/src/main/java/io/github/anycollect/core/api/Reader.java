package io.github.anycollect.core.api;

import io.github.anycollect.core.api.dispatcher.Dispatcher;

import javax.annotation.Nonnull;

public interface Reader extends Route {
    void start(@Nonnull Dispatcher dispatcher);
}
