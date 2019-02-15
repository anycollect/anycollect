package io.github.anycollect.readers.jmx.server;

import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.readers.jmx.query.JmxQuery;
import io.github.anycollect.readers.jmx.server.pool.JmxConnectionPool;

import javax.annotation.Nonnull;
import javax.management.MBeanServerConnection;
import java.util.List;
import java.util.Objects;

public final class PooledJavaApp extends JavaApp {
    private final JmxConnectionPool pool;

    public PooledJavaApp(@Nonnull final String id, @Nonnull final JmxConnectionPool pool) {
        super(id);
        Objects.requireNonNull(id, "instance id must not be null");
        Objects.requireNonNull(pool, "jmx connection pool must not be null");
        this.pool = pool;
    }

    @Nonnull
    public List<Metric> execute(@Nonnull final JmxQuery query) throws QueryException, ConnectionException {
        JmxConnection jmxConnection = null;
        try {
            jmxConnection = pool.borrowConnection();
            MBeanServerConnection connection = jmxConnection.getConnection();
            try {
                return query.executeOn(connection);
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
