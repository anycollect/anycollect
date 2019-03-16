package io.github.anycollect.readers.jmx.server;

import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.*;
import io.github.anycollect.readers.jmx.query.JmxQuery;
import io.github.anycollect.readers.jmx.server.pool.JmxConnectionPool;
import lombok.EqualsAndHashCode;

import javax.annotation.Nonnull;
import javax.management.MBeanServerConnection;
import java.util.List;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
public final class PooledJavaApp extends JavaApp {
    private final JmxConnectionPool pool;

    public PooledJavaApp(@Nonnull final String id,
                         @Nonnull final JmxConnectionPool pool) {
        super(id, Tags.empty(), Tags.empty());
        Objects.requireNonNull(pool, "jmx connection pool must not be null");
        this.pool = pool;
    }

    public PooledJavaApp(@Nonnull final String id,
                         @Nonnull final Tags tags,
                         @Nonnull final Tags meta,
                         @Nonnull final JmxConnectionPool pool,
                         @Nonnull final MeterRegistry registry) {
        super(id, tags, meta);
        Objects.requireNonNull(pool, "jmx connection pool must not be null");
        this.pool = pool;
        Gauge.make("jmx.pool.connections.live", pool, JmxConnectionPool::getNumActive)
                .concatTags(tags)
                .tag("state", "active")
                .unit("connections")
                .meta(this.getClass())
                .register(registry);
        Gauge.make("jmx.pool.connections.live", pool, JmxConnectionPool::getNumIdle)
                .concatTags(tags)
                .tag("state", "idle")
                .unit("connections")
                .meta(this.getClass())
                .register(registry);
        FunctionCounter.make("jmx.pool.connections.invalidated", pool, JmxConnectionPool::getTotalInvalidated)
                .concatTags(tags)
                .unit("connections")
                .meta(this.getClass())
                .register(registry);
    }

    @Nonnull
    public List<Metric> execute(@Nonnull final JmxQuery query) throws QueryException, ConnectionException {
        JmxConnection jmxConnection = null;
        try {
            jmxConnection = pool.borrowConnection();
            MBeanServerConnection connection = jmxConnection.getConnection();
            try {
                return query.executeOn(connection, this);
            } catch (ConnectionException e) {
                pool.invalidateConnection(jmxConnection);
                throw e;
            }
        } finally {
            if (jmxConnection != null) {
                pool.returnConnection(jmxConnection);
            }
        }
    }
}
