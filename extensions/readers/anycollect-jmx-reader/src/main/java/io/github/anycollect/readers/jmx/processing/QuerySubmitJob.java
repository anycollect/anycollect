package io.github.anycollect.readers.jmx.processing;

import io.github.anycollect.readers.jmx.query.Query;
import io.github.anycollect.readers.jmx.server.Server;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.util.Objects;

@ToString
@EqualsAndHashCode
public final class QuerySubmitJob implements Runnable {
    private final Query query;
    private final Server server;
    private final QueryExecutor executor;

    public QuerySubmitJob(@Nonnull final Query query,
                          @Nonnull final Server server,
                          @Nonnull final QueryExecutor executor) {
        Objects.requireNonNull(query, "query must not be null");
        Objects.requireNonNull(server, "server must not be null");
        Objects.requireNonNull(executor, "query executor must not be null");
        this.query = query;
        this.server = server;
        this.executor = executor;
    }

    @Nonnull
    public Query getQuery() {
        return query;
    }

    @Nonnull
    public Server getServer() {
        return server;
    }

    @Override
    public void run() {
        executor.submit(query, server);
    }
}
