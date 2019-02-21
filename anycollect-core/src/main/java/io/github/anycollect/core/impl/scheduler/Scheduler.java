package io.github.anycollect.core.impl.scheduler;

import javax.annotation.Nonnull;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public interface Scheduler {
    Cancellation scheduleAtFixedRate(@Nonnull Runnable runnable, long period, @Nonnull TimeUnit unit);

    <V> Future<V> executeImmediately(@Nonnull Callable<V> callable);

    void shutdown();

    boolean isShutdown();
}
