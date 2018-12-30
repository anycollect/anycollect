package io.github.anycollect.readers.jmx.server.pool;

import io.github.anycollect.readers.jmx.ConnectionException;
import io.github.anycollect.readers.jmx.server.JmxConnection;

import javax.annotation.Nonnull;

public interface JmxConnectionPool {
    @Nonnull
    JmxConnection borrowConnection() throws ConnectionException;

    void invalidateConnection(@Nonnull JmxConnection connection);

    void returnConnection(@Nonnull JmxConnection connection);
}
