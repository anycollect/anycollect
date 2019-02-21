package io.github.anycollect.core.impl;

import io.github.anycollect.core.impl.scheduler.Cancellation;
import io.github.anycollect.core.impl.scheduler.Scheduler;

import javax.annotation.Nonnull;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class JitScheduler implements Scheduler {
    private volatile boolean shutdown = false;

    @Override
    public Cancellation scheduleAtFixedRate(@Nonnull Runnable runnable, long period, @Nonnull TimeUnit unit) {
        runnable.run();
        return new NoopCancelation();
    }

    @Override
    public <V> Future<V> executeImmediately(@Nonnull Callable<V> callable) {
        return null;
    }

    @Override
    public void shutdown() {
        shutdown = true;
    }

    @Override
    public boolean isShutdown() {
        return shutdown;
    }
}
