package io.github.anycollect.readers.jmx.server;

import io.github.anycollect.core.api.job.Job;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.meter.api.MeterRegistry;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.readers.jmx.query.JmxQuery;
import io.github.anycollect.readers.jmx.query.operations.QueryOperation;
import io.github.anycollect.readers.jmx.server.pool.JmxConnectionPool;

import javax.annotation.Nonnull;

public interface JavaApp extends Target {
    static JavaApp create(@Nonnull String id,
                          @Nonnull Tags tags,
                          @Nonnull Tags meta,
                          @Nonnull JmxConnectionPool pool,
                          @Nonnull MeterRegistry registry) {
        return new PooledJavaApp(id, tags, meta, pool, registry);
    }

    @Nonnull
    default Job bind(@Nonnull JmxQuery query) {
        return query.bind(this);
    }

    <T> T operate(@Nonnull QueryOperation<T> operation) throws QueryException, ConnectionException;
}
