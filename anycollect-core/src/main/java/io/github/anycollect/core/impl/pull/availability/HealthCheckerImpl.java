package io.github.anycollect.core.impl.pull.availability;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.core.api.internal.Cancellation;
import io.github.anycollect.core.impl.scheduler.Scheduler;
import io.github.anycollect.metric.Tags;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class HealthCheckerImpl<T extends Target, Q extends Query<T>> implements HealthChecker<T, Q> {
    private final Dispatcher dispatcher;
    private final long periodInMillis;
    private final Scheduler healthCheckScheduler;
    @GuardedBy("lock")
    private final Map<T, Cancellation> healthCheckCancellation = new HashMap<>();
    private final Tags tags;
    private final Tags meta;
    private final Lock lock = new ReentrantLock();


    public HealthCheckerImpl(@Nonnull final Dispatcher dispatcher,
                             @Nonnull final Scheduler healthCheckScheduler,
                             final long periodInMillis,
                             @Nonnull final Tags tags,
                             @Nonnull final Tags meta) {
        this.dispatcher = dispatcher;
        this.healthCheckScheduler = healthCheckScheduler;
        this.periodInMillis = periodInMillis;
        this.tags = tags;
        this.meta = meta;
    }

    @Override
    public void add(@Nonnull final CheckingTarget<T> checkingTarget) {
        T target = checkingTarget.get();
        lock.lock();
        try {
            if (!healthCheckCancellation.containsKey(target)) {
                HealthCheck check = new HealthCheck(dispatcher, checkingTarget, tags, meta, periodInMillis);
                Cancellation cancellation
                        = healthCheckScheduler.scheduleAtFixedRate(check, periodInMillis, TimeUnit.MILLISECONDS);
                healthCheckCancellation.put(target, cancellation);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void remove(@Nonnull final CheckingTarget<T> checkingTarget) {
        T target = checkingTarget.get();
        lock.lock();
        try {
            Cancellation cancellation = healthCheckCancellation.remove(target);
            if (cancellation != null) {
                cancellation.cancel();
            }
        } finally {
            lock.unlock();
        }
    }
}
