package io.github.anycollect.readers.jmx.server;

import javax.annotation.Nonnull;

public interface JmxConnectionFactory {
    @Nonnull
    JmxConnection createJmxConnection();
}
