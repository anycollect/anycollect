package io.github.anycollect.readers.jmx.server;

import io.github.anycollect.metric.Metric;
import io.github.anycollect.readers.jmx.ConnectionException;
import io.github.anycollect.readers.jmx.QueryException;
import io.github.anycollect.readers.jmx.application.Application;
import io.github.anycollect.readers.jmx.query.Query;
import io.github.anycollect.readers.jmx.server.pool.JmxConnectionPool;

import javax.annotation.Nonnull;
import javax.management.MBeanServerConnection;
import java.util.List;
import java.util.Objects;

public final class PooledServer extends Server {
    private final JmxConnectionPool pool;

    public PooledServer(@Nonnull final String id,
                        @Nonnull final Application application,
                        @Nonnull final JmxConnectionPool pool) {
        super(id, application);
        Objects.requireNonNull(id, "server id must not be null");
        Objects.requireNonNull(application, "application must not be null");
        Objects.requireNonNull(pool, "jmx connection pool must not be null");
        this.pool = pool;
    }

    @Nonnull
    public List<Metric> execute(@Nonnull final Query query) throws QueryException, ConnectionException {
        Application application = getApplication();
        if (!application.getQueryMatcher().matches(query)) {
            throw new IllegalArgumentException("query " + query + " must not be executed on server of " + application);
        }
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
