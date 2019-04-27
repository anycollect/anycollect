package io.github.anycollect.core.impl.pull;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.api.internal.PeriodicQuery;
import io.github.anycollect.core.api.internal.State;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.core.impl.pull.availability.CheckingTarget;
import io.github.anycollect.core.impl.pull.availability.HealthChecker;
import io.github.anycollect.core.impl.scheduler.Cancellation;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.stream.Collectors.toSet;

public final class DesiredStateManagerImpl<T extends Target<Q>, Q extends Query> implements DesiredStateManager<T, Q> {
    private static final Logger LOG = LoggerFactory.getLogger(DesiredStateManagerImpl.class);
    private final PullScheduler puller;
    private final Dispatcher dispatcher;
    private final Clock clock;
    @GuardedBy("lock")
    private final Map<JobId<T, Q>, Cancellation> cancellations = new HashMap<>();
    @GuardedBy("lock")
    private final Map<T, CheckingTarget<T>> checks = new HashMap<>();
    @GuardedBy("lock")
    private State<T, Q> currentState;
    private final HealthChecker<T, Q> healthChecker;
    private final Lock lock = new ReentrantLock();

    public DesiredStateManagerImpl(@Nonnull final PullScheduler puller,
                                   @Nonnull final Dispatcher dispatcher,
                                   @Nonnull final HealthChecker<T, Q> healthChecker) {
        this.puller = puller;
        this.dispatcher = dispatcher;
        this.healthChecker = healthChecker;
        this.currentState = State.empty();
        this.clock = Clock.getDefault();
    }

    DesiredStateManagerImpl(@Nonnull final PullScheduler puller,
                            @Nonnull final Dispatcher dispatcher,
                            @Nonnull final Clock clock) {
        this.puller = puller;
        this.dispatcher = dispatcher;
        this.healthChecker = HealthChecker.noop();
        this.currentState = State.empty();
        this.clock = clock;
    }

    @Override
    public void update(@Nonnull final State<T, Q> desiredState) {
        lock.lock();
        int cancelledQueries = 0;
        int newQueries = 0;
        try {
            State<T, Q> previousState = currentState;
            Set<T> previousTargets = previousState.getTargets();
            Set<T> desiredTargets = desiredState.getTargets();
            for (T target : previousTargets) {
                if (!desiredTargets.contains(target)) {
                    puller.release(target);
                    cancelledQueries += previousState.getQueries(target).size();
                    healthChecker.remove(checks.get(target));
                    checks.remove(target);
                }
            }
            for (T target : desiredTargets) {
                CheckingTarget<T> checkingTarget;
                if (!previousTargets.contains(target)) {
                    checkingTarget = new CheckingTarget<>(target, clock.wallTime());
                    healthChecker.add(checkingTarget);
                    checks.put(target, checkingTarget);
                } else {
                    checkingTarget = checks.get(target);
                }
                Set<PeriodicQuery<Q>> previousQueries = previousState.getQueries(target);
                Set<PeriodicQuery<Q>> desiredQueries = desiredState.getQueries(target);
                for (PeriodicQuery<Q> periodicQuery : previousQueries) {
                    if (!desiredQueries.contains(periodicQuery)) {
                        Q query = periodicQuery.getQuery();
                        int period = periodicQuery.getPeriodInSeconds();
                        JobId<T, Q> id = new JobId<>(target, query, period);
                        cancellations.get(id).cancel();
                        cancelledQueries++;
                    }
                }
                for (PeriodicQuery<Q> periodicQuery : desiredQueries) {
                    if (!previousQueries.contains(periodicQuery)) {
                        Q query = periodicQuery.getQuery();
                        int period = periodicQuery.getPeriodInSeconds();
                        Cancellation cancellation = puller.schedulePull(checkingTarget, query, dispatcher, period);
                        JobId<T, Q> id = new JobId<>(target, query, period);
                        cancellations.put(id, cancellation);
                        newQueries++;
                    }
                }
            }
            this.currentState = desiredState;
            LOG.debug("desired state has been successfully updated: cancelled queries: {}, new queries: {}",
                    cancelledQueries, newQueries);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void cleanup() {
        lock.lock();
        try {
            for (Cancellation cancellation : cancellations.values()) {
                cancellation.cancel();
            }
            Set<T> targets = cancellations.keySet().stream().map(job -> job.target).collect(toSet());
            for (T target : targets) {
                puller.release(target);
            }
            checks.clear();
            cancellations.clear();
            currentState = State.empty();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void destroy() {
        cleanup();
    }

    @EqualsAndHashCode
    private static final class JobId<T extends Target<Q>, Q extends Query> {
        private final T target;
        private final Q query;
        private final int period;

        JobId(final T target, final Q query, final int period) {
            this.target = target;
            this.query = query;
            this.period = period;
        }
    }
}
