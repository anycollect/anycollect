package io.github.anycollect.core.impl;

import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.Metric;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class PullJob<T extends Target<Q>, Q extends Query> implements Runnable {
    @Getter
    private final T target;
    @Getter
    private final Q query;
    private final ResultCallback<T, Q> callback;
    private final Clock clock;

    private long submissionTime;
    private long startTime;
    private long endTime;
    private State state;

    private final Lock lock = new ReentrantLock();

    public PullJob(@Nonnull final T target,
                   @Nonnull final Q query,
                   @Nonnull final ResultCallback<T, Q> callback,
                   @Nonnull final Clock clock) {
        Objects.requireNonNull(target, "target must not be null");
        Objects.requireNonNull(query, "query must not be null");
        Objects.requireNonNull(callback, "callback must not be null");
        this.target = target;
        this.query = query;
        this.callback = callback;
        this.clock = clock;
        this.state = State.INITIALIZED;
    }

    public void submit() {
        lock.lock();
        try {
            this.state = State.WAITING;
            this.submissionTime = clock.time();
        } finally {
            lock.unlock();
        }
    }

    private long getWaitingTime() {
        lock.lock();
        try {
            return startTime - submissionTime;
        } finally {
            lock.unlock();
        }
    }

    private long getProcessingTime() {
        lock.lock();
        try {
            return endTime - startTime;
        } finally {
            lock.unlock();
        }
    }

    public State getState() {
        lock.lock();
        try {
            return state;
        } finally {
            lock.unlock();
        }
    }

    public void renew() {
        lock.lock();
        try {
            if (state == State.COMPLETED) {
                this.submissionTime = 0L;
                this.startTime = 0L;
                this.endTime = 0L;
                this.state = State.INITIALIZED;
            } else {
                throw new IllegalStateException("job must be completed to be renewed");
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void run() {
        preRun();
        try {
            List<Metric> metrics = target.execute(query);
            complete();
            callback.call(Result.success(target, query, metrics, getWaitingTime(), getProcessingTime()));
        } catch (QueryException | ConnectionException | RuntimeException e) {
            complete();
            callback.call(Result.fail(target, query, e, getWaitingTime(), getProcessingTime()));
        }
    }

    private void preRun() {
        lock.lock();
        try {
            startTime = clock.time();
            state = State.RUNNING;
        } finally {
            lock.unlock();
        }
    }

    private void complete() {
        lock.lock();
        try {
            endTime = clock.time();
            state = State.COMPLETED;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        return target.getLabel() + ".execute(" + query.getGroup() + ":" + query.getLabel() + ")";
    }

    public enum State {
        INITIALIZED, WAITING, RUNNING, COMPLETED
    }
}
