package io.github.anycollect.readers.jmx.server;

import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.readers.jmx.query.JmxQuery;

import javax.annotation.Nonnull;
import java.util.List;

public final class SimpleServer extends JavaApp {
    private final JmxConnection connection;

    public SimpleServer(@Nonnull final String id,
                        @Nonnull final JmxConnection connection) {
        super(id);
        this.connection = connection;
    }

    @Nonnull
    @Override
    public List<Metric> execute(@Nonnull final JmxQuery query) throws QueryException, ConnectionException {
        return query.executeOn(connection.getConnection());
    }
}
