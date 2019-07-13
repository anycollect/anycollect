package io.github.anycollect.core.impl.scheduler;


import io.github.anycollect.core.api.internal.Cancellation;
import io.github.anycollect.metric.MeterRegistry;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.metric.noop.NoopMeterRegistry;

import javax.annotation.Nonnull;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class SchedulerImpl implements Scheduler {
    private final ScheduledThreadPoolExecutor service;
    private final MeterRegistry registry;
    private final String prefix;
    private final Tags tags;

    public SchedulerImpl(@Nonnull final ScheduledThreadPoolExecutor service) {
        this.service = service;
        this.registry = new NoopMeterRegistry();
        this.prefix = "";
        this.tags = Tags.empty();
    }

    public SchedulerImpl(@Nonnull final ScheduledThreadPoolExecutor service,
                         @Nonnull final MeterRegistry registry,
                         @Nonnull final String prefix,
                         @Nonnull final Tags tags) {
        this.service = service;
        this.registry = registry;
        this.prefix = prefix;
        this.tags = tags;
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
    public Cancellation scheduleAtFixedRate(@Nonnull final Runnable runnable,
                                            final long period,
                                            @Nonnull final TimeUnit unit,
                                            final boolean allowOverworkAfterPause) {
        if (allowOverworkAfterPause) {
            return scheduleAtFixedRate(runnable, period, unit);
        } else {
            if (isShutdown()) {
                throw new IllegalStateException("scheduler is shutdown");
            }
            ThrottledRunnable throttledRunnable = new ThrottledRunnable(runnable, unit.toNanos(period), registry, prefix, tags);
            ScheduledFuture<?> future = service.scheduleAtFixedRate(throttledRunnable, 0L, period, unit);
            throttledRunnable.setDelayed(future);
            return new ScheduledFeatureAdapter(future);
        }
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
