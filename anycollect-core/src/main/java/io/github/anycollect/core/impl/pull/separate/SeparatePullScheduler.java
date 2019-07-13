package io.github.anycollect.core.impl.pull.separate;

import com.google.common.annotations.VisibleForTesting;
import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.core.impl.pull.PullJob;
import io.github.anycollect.core.impl.pull.PullScheduler;
import io.github.anycollect.core.impl.pull.availability.CheckingTarget;
import io.github.anycollect.core.api.internal.Cancellation;
import io.github.anycollect.core.impl.scheduler.Scheduler;
import io.github.anycollect.metric.MeterRegistry;
import io.github.anycollect.metric.noop.NoopMeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class SeparatePullScheduler implements PullScheduler {
    private static final Logger LOG = LoggerFactory.getLogger(SeparatePullScheduler.class);
    private final ConcurrentHashMap<Target, Scheduler> activeSchedulers = new ConcurrentHashMap<>();
    private final SchedulerFactory factory;
    private final MeterRegistry registry;
    private final Clock clock;
    private final String name;
    private volatile boolean stopped = false;

    @VisibleForTesting
    SeparatePullScheduler(@Nonnull final SchedulerFactory factory,
                          @Nonnull final Clock clock) {
        this.factory = factory;
        this.registry = new NoopMeterRegistry();
        this.clock = clock;
        this.name = "scheduler";
    }

    public SeparatePullScheduler(@Nonnull final SchedulerFactory factory,
                                 @Nonnull final MeterRegistry registry,
                                 @Nonnull final String name) {
        this.factory = factory;
        this.registry = registry;
        this.name = name;
        this.clock = Clock.getDefault();
    }

    @Nonnull
    @Override
    public <T extends Target, Q extends Query<T>> Cancellation schedulePull(
            @Nonnull final CheckingTarget<T> target,
            @Nonnull final Q query,
            @Nonnull final Dispatcher dispatcher,
            final int periodInSeconds) {
        if (stopped) {
            return Cancellation.NOOP;
        }
        PullJob<T, Q> job = new PullJob<>(target, query, name, dispatcher, registry, clock);
        Scheduler scheduler = activeSchedulers.computeIfAbsent(target.get(), aTarget -> factory.create(aTarget, name));
        return scheduler.scheduleAtFixedRate(job, periodInSeconds, TimeUnit.SECONDS, false);
    }

    @Override
    public void release(@Nonnull final Target target) {
        if (stopped) {
            return;
        }
        Scheduler scheduler = activeSchedulers.remove(target);
        if (scheduler != null) {
            LOG.info("Stopping scheduler for {}", target.getId());
            // all jobs associated with target will be terminated
            scheduler.shutdown();
        }
    }

    @Override
    public void shutdown() {
        if (stopped) {
            return;
        }
        stopped = true;
        LOG.info("Stopping separate pull scheduler {}", name);
        for (Map.Entry<Target, Scheduler> entry : activeSchedulers.entrySet()) {
            Target target = entry.getKey();
            Scheduler scheduler = entry.getValue();
            LOG.info("Stopping scheduler for {}", target.getId());
            scheduler.shutdown();
        }
    }
}
