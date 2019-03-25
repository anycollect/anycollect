package io.github.anycollect.readers.jmx.server;

import javax.annotation.Nonnull;

public interface JmxEventListener {
    void handle(@Nonnull JmxEvent event);
}
