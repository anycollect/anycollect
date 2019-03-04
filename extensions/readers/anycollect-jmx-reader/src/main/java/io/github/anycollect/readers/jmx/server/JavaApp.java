package io.github.anycollect.readers.jmx.server;

import io.github.anycollect.core.api.target.AbstractTarget;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.MeterRegistry;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.readers.jmx.query.JmxQuery;
import io.github.anycollect.readers.jmx.server.pool.JmxConnectionPool;
import lombok.EqualsAndHashCode;

import javax.annotation.Nonnull;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
public abstract class JavaApp extends AbstractTarget<JmxQuery> {
    protected JavaApp(@Nonnull final String id, @Nonnull final Tags tags) {
        super(id, tags);
    }

    public static JavaApp create(@Nonnull final String id,
                                 @Nonnull final JmxConnectionPool pool,
                                 @Nonnull final MeterRegistry registry) {
        return new PooledJavaApp(id, Tags.of("instance", id), pool, registry);
    }

    @Nonnull
    public abstract List<Metric> execute(@Nonnull JmxQuery query) throws QueryException, ConnectionException;
}
