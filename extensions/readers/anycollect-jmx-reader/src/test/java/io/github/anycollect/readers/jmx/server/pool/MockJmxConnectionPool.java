package io.github.anycollect.readers.jmx.server.pool;

import io.github.anycollect.readers.jmx.ConnectionException;
import io.github.anycollect.readers.jmx.server.JmxConnection;
import io.github.anycollect.readers.jmx.server.JmxConnectionFactory;

import javax.annotation.Nonnull;
import java.io.IOException;

public class MockJmxConnectionPool implements JmxConnectionPool {
    private JmxConnection jmxConnection;

    public MockJmxConnectionPool(JmxConnectionFactory jmxConnectionFactory) {
        try {
            this.jmxConnection = jmxConnectionFactory.createJmxConnection();
        } catch (ConnectionException e) {
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    @Override
    public JmxConnection borrowConnection() throws ConnectionException {
        JmxConnection ret = jmxConnection;
        jmxConnection = null;
        return ret;
    }

    @Override
    public void invalidateConnection(@Nonnull JmxConnection connection) {
        jmxConnection.markAsDestroyed();
        try {
            jmxConnection.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void returnConnection(@Nonnull JmxConnection connection) {
        jmxConnection = connection;
    }

    @Override
    public int getNumActive() {
        return 0;
    }

    @Override
    public int getNumIdle() {
        return 0;
    }

    @Override
    public long getTotalInvalidated() {
        return 0;
    }
}
