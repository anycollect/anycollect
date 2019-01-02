package io.github.anycollect.readers.jmx.processing;

import io.github.anycollect.metric.Metric;
import io.github.anycollect.readers.jmx.query.Query;
import io.github.anycollect.readers.jmx.server.Server;
import lombok.EqualsAndHashCode;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

@EqualsAndHashCode
public final class QueryJob implements Runnable {
    private final Query query;
    private final Server server;
    private final ResultCallback callback;

    public QueryJob(@Nonnull final Query query,
                    @Nonnull final Server server,
                    @Nonnull final ResultCallback callback) {
        Objects.requireNonNull(query, "query must not be null");
        Objects.requireNonNull(server, "server must not be null");
        Objects.requireNonNull(callback, "callback must not be null");
        this.query = query;
        this.server = server;
        this.callback = callback;
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
        QueryResult result;
        long startTime = System.nanoTime();
        try {
            List<Metric> metrics = server.execute(query);
            result = QueryResult.success(this, metrics, Duration.ofNanos(System.nanoTime() - startTime));
        } catch (Exception ex) {
            result = QueryResult.fail(this, ex, Duration.ofNanos(System.nanoTime() - startTime));
        }
        callback.call(result);
    }
}
