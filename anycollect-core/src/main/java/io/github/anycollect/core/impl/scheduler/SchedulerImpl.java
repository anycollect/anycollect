package io.github.anycollect.core.impl.scheduler;


import javax.annotation.Nonnull;
import java.util.concurrent.*;

public final class SchedulerImpl implements Scheduler {
    private final ScheduledThreadPoolExecutor service;

    public SchedulerImpl(@Nonnull final ScheduledThreadPoolExecutor service) {
        this.service = service;
    }

    @Override
    public Cancellation scheduleAtFixedRate(@Nonnull final Runnable runnable,
                                            final long period,
                                            @Nonnull final TimeUnit unit) {
        if (isShutdown()) {
            throw new IllegalStateException("scheduler is shutdown");
        }
        ScheduledFuture<?> future = service.scheduleAtFixedRate(runnable, 0L, period, unit);
        return new ScheduledFeatureAdapter(future);
    }

    @Override
    public <V> Future<V> executeImmediately(@Nonnull final Callable<V> callable) {
        // TODO priority
        return service.schedule(callable, 0L, TimeUnit.NANOSECONDS);
    }

    @Override
    public void shutdown() {
        service.shutdown();
    }

    @Override
    public boolean isShutdown() {
        return service.isShutdown();
    }

    public int getPoolSize() {
        return service.getCorePoolSize();
    }

    final class ScheduledFeatureAdapter implements Cancellation {
        private final ScheduledFuture<?> future;

        ScheduledFeatureAdapter(@Nonnull final ScheduledFuture<?> future) {
            this.future = future;
        }

        @Override
        public void cancel() {
            future.cancel(true);
        }
    }
}
