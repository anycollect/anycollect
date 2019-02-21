package io.github.anycollect.readers.jmx.server;

import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.MetricFamily;
import io.github.anycollect.metric.Tags;
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
    private final Tags tags;

    public PooledJavaApp(@Nonnull final String id, @Nonnull final JmxConnectionPool pool) {
        super(id);
        Objects.requireNonNull(pool, "jmx connection pool must not be null");
        this.pool = pool;
        this.tags = Tags.of("instance", id);
    }

    @Nonnull
    @Override
    public Tags getTags() {
        return tags;
    }

    @Nonnull
    public List<MetricFamily> execute(@Nonnull final JmxQuery query) throws QueryException, ConnectionException {
        JmxConnection jmxConnection = null;
        try {
            jmxConnection = pool.borrowConnection();
            MBeanServerConnection connection = jmxConnection.getConnection();
            try {
                return query.executeOn(connection, getTags());
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
