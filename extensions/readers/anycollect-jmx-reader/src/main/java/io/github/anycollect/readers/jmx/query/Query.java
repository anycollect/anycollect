package io.github.anycollect.readers.jmx.query;

import io.github.anycollect.metric.Metric;
import io.github.anycollect.readers.jmx.QueryException;
import io.github.anycollect.readers.jmx.ConnectionException;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.management.MBeanServerConnection;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

@ToString
@EqualsAndHashCode
public abstract class Query {
    @Nonnull
    private final QueryId id;
    @Nullable
    private final Duration interval;

    public Query(@Nonnull final String group, @Nonnull final String label, @Nullable final Duration interval) {
        this(new QueryId(group, label), interval);
    }

    public Query(@Nonnull final QueryId id, final Duration interval) {
        this.id = id;
        this.interval = interval;
    }

    @Nonnull
    public abstract List<Metric> executeOn(@Nonnull MBeanServerConnection connection)
            throws QueryException, ConnectionException;

    @Nonnull
    public final QueryId getId() {
        return id;
    }

    @Nonnull
    public final String getGroup() {
        return id.getGroup();
    }

    @Nonnull
    public final String getLabel() {
        return id.getLabel();
    }

    @Nonnull
    public final Optional<Duration> getInterval() {
        return Optional.ofNullable(interval);
    }
}
