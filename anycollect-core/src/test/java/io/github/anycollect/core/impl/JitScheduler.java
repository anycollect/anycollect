package io.github.anycollect.core.impl;

import io.github.anycollect.core.api.internal.Cancellation;
import io.github.anycollect.core.impl.scheduler.Scheduler;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

public class JitScheduler implements Scheduler {
    private volatile boolean shutdown = false;

    @Override
    public Cancellation scheduleAtFixedRate(@Nonnull Runnable runnable, long period, @Nonnull TimeUnit unit) {
        runnable.run();
        return new NoopCancelation();
    }

    @Override
    public Cancellation scheduleAtFixedRate(@Nonnull Runnable runnable, long period, @Nonnull TimeUnit unit, boolean allowOverworkAfterPause) {
        runnable.run();
        return new NoopCancelation();
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
