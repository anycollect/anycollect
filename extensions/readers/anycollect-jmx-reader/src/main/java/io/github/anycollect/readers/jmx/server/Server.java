package io.github.anycollect.readers.jmx.server;

import io.github.anycollect.metric.Metric;
import io.github.anycollect.readers.jmx.ConnectionException;
import io.github.anycollect.readers.jmx.QueryException;
import io.github.anycollect.readers.jmx.application.Application;
import io.github.anycollect.readers.jmx.query.Query;
import io.github.anycollect.readers.jmx.server.pool.JmxConnectionPool;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

@ToString
@EqualsAndHashCode
public abstract class Server {
    private final String id;
    private final Application application;

    public Server(@Nonnull final String id,
                  @Nonnull final Application application) {
        Objects.requireNonNull(id, "server id must not be null");
        Objects.requireNonNull(application, "application must not be null");
        this.id = id;
        this.application = application;
    }

    public static Server create(@Nonnull final String id,
                                @Nonnull final Application application,
                                @Nonnull final JmxConnectionPool pool) {
        return new PooledServer(id, application, pool);
    }

    @Nonnull
    public abstract List<Metric> execute(@Nonnull Query query) throws QueryException, ConnectionException;

    @Nonnull
    public final Application getApplication() {
        return application;
    }

    @Nonnull
    public final String getId() {
        return id;
    }
}
