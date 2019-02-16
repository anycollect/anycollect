package io.github.anycollect.readers.jmx.server;

import io.github.anycollect.core.api.target.AbstractTarget;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.readers.jmx.query.JmxQuery;
import io.github.anycollect.readers.jmx.server.pool.JmxConnectionPool;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.util.List;

@ToString
@EqualsAndHashCode(callSuper = true)
public abstract class JavaApp extends AbstractTarget<JmxQuery> {
    protected JavaApp(@Nonnull final String id) {
        super(id);
    }

    public static JavaApp create(@Nonnull final String id, @Nonnull final JmxConnectionPool pool) {
        return new PooledJavaApp(id, pool);
    }

    @Nonnull
    public abstract List<Metric> execute(@Nonnull JmxQuery query) throws QueryException, ConnectionException;
}
