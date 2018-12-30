package io.github.anycollect.readers.jmx.server.pool;

import io.github.anycollect.readers.jmx.server.JmxConnectionFactory;

import javax.annotation.Nonnull;

public class MockJmxConnectionPoolFactory implements JmxConnectionPoolFactory {
    @Nonnull
    @Override
    public MockJmxConnectionPool create(@Nonnull JmxConnectionFactory jmxConnectionFactory) {
        return new MockJmxConnectionPool(jmxConnectionFactory);
    }
}
