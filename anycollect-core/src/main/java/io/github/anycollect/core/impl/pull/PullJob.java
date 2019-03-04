package io.github.anycollect.core.impl.pull;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.Counter;
import io.github.anycollect.metric.MeterRegistry;
import io.github.anycollect.metric.noop.NoopMeterRegistry;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class PullJob<T extends Target<Q>, Q extends Query> implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(PullJob.class);
    @Getter
    private final T target;
    @Getter
    private final Q query;
    private final Dispatcher dispatcher;
    private final Clock clock;
    private final Counter failed;
    private final Counter succeeded;

    public PullJob(@Nonnull final T target,
                   @Nonnull final Q query,
                   @Nonnull final Dispatcher dispatcher) {
        this(target, query, dispatcher, new NoopMeterRegistry(), Clock.getDefault());
    }

    public PullJob(@Nonnull final T target,
                   @Nonnull final Q query,
                   @Nonnull final Dispatcher dispatcher,
                   @Nonnull final MeterRegistry registry,
                   @Nonnull final Clock clock) {
        Objects.requireNonNull(target, "target must not be null");
        Objects.requireNonNull(query, "query must not be null");
        Objects.requireNonNull(registry, "registry must not be null");
        Objects.requireNonNull(dispatcher, "dispatcher must not be null");
        Objects.requireNonNull(clock, "clock must not be null");
        this.target = target;
        this.query = query;
        this.dispatcher = dispatcher;
        this.clock = clock;
        this.failed = Counter.key("pull.jobs.failed")
                .unit("jobs")
                .tag("target", target.getId())
                .register(registry);
        this.succeeded = Counter.key("pull.jobs.failed")
                .unit("jobs")
                .tag("target", target.getId())
                .register(registry);
    }

    @Override
    public void run() {
        long start = clock.wallTime();
        try {
            target.execute(query, dispatcher);
            failed.increment();
            LOG.debug("success: {}.execute({}) taken {}ms", target, query, clock.wallTime() - start);
        } catch (QueryException | ConnectionException | RuntimeException e) {
            succeeded.increment();
            LOG.debug("failed: {}.execute({}) taken {}ms and failed", target, query, clock.wallTime() - start, e);
        }
    }

    @Override
    public String toString() {
        return target.getId() + ".execute(" + query.getId() + ")";
    }
}
