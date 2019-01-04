package io.github.anycollect.readers.jmx.server;

import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.MetricId;
import io.github.anycollect.metric.Type;
import io.github.anycollect.readers.jmx.ConnectionException;
import io.github.anycollect.readers.jmx.QueryException;
import io.github.anycollect.readers.jmx.application.Application;
import io.github.anycollect.readers.jmx.monitoring.MetricRegistry;
import io.github.anycollect.readers.jmx.query.Query;
import io.github.anycollect.readers.jmx.server.pool.JmxConnectionPool;

import javax.annotation.Nonnull;
import javax.management.MBeanServerConnection;
import java.util.List;
import java.util.Objects;

import static io.github.anycollect.readers.jmx.monitoring.MonitoringConstants.*;

public final class PooledServer extends Server {
    private final JmxConnectionPool pool;

    public PooledServer(@Nonnull final String id,
                        @Nonnull final Application application,
                        @Nonnull final JmxConnectionPool pool,
                        @Nonnull final MetricRegistry metricRegistry) {
        super(id, application);
        Objects.requireNonNull(id, "server id must not be null");
        Objects.requireNonNull(application, "application must not be null");
        Objects.requireNonNull(pool, "jmx connection pool must not be null");
        Objects.requireNonNull(metricRegistry, "metric registry must not be null");
        this.pool = pool;
        MetricId.Builder builder = MetricId.builder()
                .unit(CONNECTIONS_UNIT)
                .tag(APPLICATION_TAG, application.getName())
                .tag(SERVER_TAG, id);
        MetricId idle = builder.key(CONNECTION_POOL_IDLE).type(Type.GAUGE).build();
        MetricId active = builder.key(CONNECTION_POOL_ACTIVE).type(Type.GAUGE).build();
        MetricId invalidated = builder.key(CONNECTION_POOL_INVALIDATED).type(Type.COUNTER).build();
        metricRegistry.register(idle, pool::getNumIdle);
        metricRegistry.register(active, pool::getNumActive);
        metricRegistry.register(invalidated, pool::getTotalInvalidated);
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