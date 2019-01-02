package io.github.anycollect.readers.jmx.server;

import io.github.anycollect.metric.Metric;
import io.github.anycollect.readers.jmx.ConnectionException;
import io.github.anycollect.readers.jmx.QueryException;
import io.github.anycollect.readers.jmx.application.Application;
import io.github.anycollect.readers.jmx.query.Query;

import javax.annotation.Nonnull;
import java.util.List;

public final class SimpleServer extends Server {
    private final JmxConnection connection;

    public SimpleServer(@Nonnull final String id,
                        @Nonnull final Application application,
                        @Nonnull final JmxConnection connection) {
        super(id, application);
        this.connection = connection;
    }

    @Nonnull
    @Override
    public List<Metric> execute(@Nonnull final Query query) throws QueryException, ConnectionException {
        return query.executeOn(connection.getConnection());
    }
}
