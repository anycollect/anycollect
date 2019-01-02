package io.github.anycollect.readers.jmx.processing;

import io.github.anycollect.metric.Metric;
import io.github.anycollect.readers.jmx.query.Query;
import io.github.anycollect.readers.jmx.server.Server;
import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.time.Duration;
import java.util.*;

@Immutable
@ToString
public final class QueryResult {
    private final Query query;
    private final Server server;
    private final List<Metric> metrics;
    private final boolean success;
    private final Exception exception;
    private final Duration duration;

    private QueryResult(@Nonnull final QueryJob job,
                        @Nullable final List<Metric> metrics,
                        final boolean success,
                        @Nullable final Exception exception,
                        @Nonnull final Duration duration) {
        this.query = job.getQuery();
        this.server = job.getServer();
        this.metrics = metrics != null ? new ArrayList<>(metrics) : null;
        this.success = success;
        this.exception = exception;
        this.duration = duration;
    }

    @Nonnull
    public static QueryResult success(@Nonnull final QueryJob job,
                                      @Nonnull final List<Metric> metrics,
                                      @Nonnull final Duration duration) {
        Objects.requireNonNull(job, "query job must not be null");
        Objects.requireNonNull(metrics, "metrics must not be null");
        Objects.requireNonNull(duration, "duration must not be null");
        return new QueryResult(job, metrics, true, null, duration);
    }

    @Nonnull
    public static QueryResult fail(@Nonnull final QueryJob job,
                                   @Nonnull final Exception exception,
                                   @Nonnull final Duration duration) {
        Objects.requireNonNull(job, "query job must not be null");
        Objects.requireNonNull(exception, "exception must not be null");
        Objects.requireNonNull(duration, "duration must not be null");
        return new QueryResult(job, null, false, exception, duration);
    }

    @Nonnull
    public Query getQuery() {
        return query;
    }

    @Nonnull
    public Server getServer() {
        return server;
    }

    @Nonnull
    public List<Metric> getMetrics() {
        if (!success) {
            throw new IllegalStateException("result is not success");
        }
        return Collections.unmodifiableList(metrics);
    }

    public boolean isSuccess() {
        return success;
    }

    @Nonnull
    public Optional<Exception> getException() {
        return Optional.ofNullable(exception);
    }

    @Nonnull
    public Duration getDuration() {
        return duration;
    }
}
