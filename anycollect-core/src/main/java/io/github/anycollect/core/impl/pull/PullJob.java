package io.github.anycollect.core.impl.pull;

import io.github.anycollect.core.api.job.Job;
import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.Counter;
import io.github.anycollect.metric.MeterRegistry;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.noop.NoopMeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public final class PullJob<T extends Target<Q>, Q extends Query> implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(PullJob.class);
    private final T target;
    private final Q query;
    private final Job job;
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
        this.job = target.bind(query);
        this.dispatcher = dispatcher;
        this.clock = clock;
        this.failed = Counter.key("pull.jobs.failed")
                .unit("jobs")
                .tag("target", target.getId())
                .meta(this.getClass())
                .register(registry);
        this.succeeded = Counter.key("pull.jobs.succeeded")
                .unit("jobs")
                .tag("target", target.getId())
                .meta(this.getClass())
                .register(registry);
    }

    @Override
    public void run() {
        long start = clock.wallTime();
        try {
            List<Metric> metrics;
            synchronized (job) {
                metrics = job.execute();
            }
            succeeded.increment();
            dispatcher.dispatch(metrics);
            LOG.debug("success: {}.execute({}) taken {}ms and produces {} metrics", target.getId(), query.getId(),
                    clock.wallTime() - start, metrics.size());
        } catch (QueryException | ConnectionException | RuntimeException e) {
            failed.increment();
            LOG.debug("failed: {}.execute({}) taken {}ms and failed", target.getId(), query.getId(),
                    clock.wallTime() - start, e);
        }
    }
}
