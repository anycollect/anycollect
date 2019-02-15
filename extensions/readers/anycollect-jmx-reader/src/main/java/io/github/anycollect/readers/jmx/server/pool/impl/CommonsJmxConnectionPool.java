package io.github.anycollect.readers.jmx.server.pool.impl;

import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.readers.jmx.server.JmxConnection;
import io.github.anycollect.readers.jmx.server.pool.JmxConnectionPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.atomic.LongAdder;

public final class CommonsJmxConnectionPool implements JmxConnectionPool {
    private static final Logger LOG = LoggerFactory.getLogger(CommonsJmxConnectionPool.class);
    private final GenericObjectPool<JmxConnection> pool;
    private final LongAdder totalInvalidated = new LongAdder();

    public CommonsJmxConnectionPool(@Nonnull final GenericObjectPool<JmxConnection> pool) {
        Objects.requireNonNull(pool, "commons pool must not be null");
        this.pool = pool;
    }

    @Nonnull
    @Override
    public JmxConnection borrowConnection() throws ConnectionException {
        try {
            return pool.borrowObject();
        } catch (NoSuchElementException e) {
            throw new ConnectionException("unable to borrow jmx connection from pool due to timeout");
        } catch (ConnectionException e) {
            throw e;
        } catch (Exception e) {
            LOG.warn("unable to borrow connection due to unexpected error, this should never happen", e);
            throw new ConnectionException("unable to borrow connection from pool due to unexpected error", e);
        }
    }

    @Override
    public void invalidateConnection(@Nonnull final JmxConnection connection) {
        try {
            totalInvalidated.increment();
            pool.invalidateObject(connection);
        } catch (Exception e) {
            LOG.warn("unable to invalidate connection {}, this should never happen", connection, e);
        }
    }

    @Override
    public void returnConnection(@Nonnull final JmxConnection connection) {
        try {
            pool.returnObject(connection);
        } catch (Exception e) {
            LOG.warn("unable to return connection {}, this should never happen", connection, e);
        }
    }

    @Override
    public int getNumActive() {
        return pool.getNumActive();
    }

    @Override
    public int getNumIdle() {
        return pool.getNumIdle();
    }

    @Override
    public long getTotalInvalidated() {
        return totalInvalidated.longValue();
    }
}
