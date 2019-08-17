package io.github.anycollect.readers.jmx.server;

import io.github.anycollect.core.api.target.AbstractTarget;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.meter.api.FunctionCounter;
import io.github.anycollect.meter.api.Gauge;
import io.github.anycollect.meter.api.MeterRegistry;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.readers.jmx.query.operations.QueryOperation;
import io.github.anycollect.readers.jmx.server.pool.JmxConnectionPool;
import lombok.EqualsAndHashCode;

import javax.annotation.Nonnull;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
public final class PooledJavaApp extends AbstractTarget implements JavaApp {
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
        Gauge.make("jmx/connection.pool/live.connections", pool, JmxConnectionPool::getNumActive)
                .concatTags(tags)
                .tag("state", "active")
                .meta(this.getClass())
                .register(registry);
        Gauge.make("jmx/connection.pool/live.connections", pool, JmxConnectionPool::getNumIdle)
                .concatTags(tags)
                .tag("state", "idle")
                .meta(this.getClass())
                .register(registry);
        FunctionCounter.make("jmx/connection.pool/invalidated.connections", pool, JmxConnectionPool::getTotalInvalidated)
                .concatTags(tags)
                .meta(this.getClass())
                .register(registry);
    }

    @Override
    public <T> T operate(@Nonnull final QueryOperation<T> operation) throws QueryException, ConnectionException {
        JmxConnection jmxConnection = null;
        try {
            jmxConnection = pool.borrowConnection();
            try {
                return operation.operate(jmxConnection);
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
