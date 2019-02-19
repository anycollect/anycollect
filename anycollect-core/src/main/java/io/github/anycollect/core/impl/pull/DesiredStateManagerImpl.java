package io.github.anycollect.core.impl.pull;

import io.github.anycollect.core.api.internal.PeriodicQuery;
import io.github.anycollect.core.api.internal.State;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;
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
    private final ResultCallback<T, Q> callback;
    @GuardedBy("lock")
    private final Map<JobId<T, Q>, Cancellation> cancellations = new HashMap<>();
    @GuardedBy("lock")
    private State<T, Q> currentState;
    private final Lock lock = new ReentrantLock();

    public DesiredStateManagerImpl(@Nonnull final PullScheduler puller,
                                   @Nonnull final ResultCallback<T, Q> callback) {
        this.puller = puller;
        this.callback = callback;
        this.currentState = State.empty();
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
                }
            }
            for (T target : desiredTargets) {
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
                        Cancellation cancellation = puller.schedulePull(target, query, callback, period);
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
