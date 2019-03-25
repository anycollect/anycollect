package io.github.anycollect.readers.jmx.server;

import io.github.anycollect.core.api.job.Job;
import io.github.anycollect.core.api.target.AbstractTarget;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.MeterRegistry;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.readers.jmx.query.JmxQuery;
import io.github.anycollect.readers.jmx.query.operations.QueryOperation;
import io.github.anycollect.readers.jmx.server.pool.JmxConnectionPool;
import lombok.EqualsAndHashCode;

import javax.annotation.Nonnull;

@EqualsAndHashCode(callSuper = true)
public abstract class JavaApp extends AbstractTarget<JmxQuery> {
    protected JavaApp(@Nonnull final String id, @Nonnull final Tags tags, @Nonnull final Tags meta) {
        super(id, tags, meta);
    }

    public static JavaApp create(@Nonnull final String id,
                                 @Nonnull final Tags tags,
                                 @Nonnull final Tags meta,
                                 @Nonnull final JmxConnectionPool pool,
                                 @Nonnull final MeterRegistry registry) {
        return new PooledJavaApp(id, tags, meta, pool, registry);
    }

    @Nonnull
    @Override
    public final Job bind(@Nonnull final JmxQuery query) {
        return query.bind(this);
    }

    public abstract <T> T operate(@Nonnull QueryOperation<T> operation) throws QueryException, ConnectionException;
}
