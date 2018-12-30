package io.github.anycollect.readers.jmx.server.pool;

import io.github.anycollect.readers.jmx.server.JmxConnectionFactory;

import javax.annotation.Nonnull;

public interface JmxConnectionPoolFactory {
    @Nonnull
    JmxConnectionPool create(@Nonnull JmxConnectionFactory jmxConnectionFactory);
}
