package io.github.anycollect.readers.jmx.server;

import io.github.anycollect.core.exceptions.ConnectionException;

import javax.annotation.Nonnull;

public interface JmxConnectionFactory {
    @Nonnull
    JmxConnection createJmxConnection() throws ConnectionException;
}
