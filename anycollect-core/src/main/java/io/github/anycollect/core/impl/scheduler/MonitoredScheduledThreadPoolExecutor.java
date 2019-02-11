package io.github.anycollect.core.impl.scheduler;

import io.github.anycollect.core.api.internal.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

public final class MonitoredScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {
    private static final double ONE_HUNDRED_PERCENTS = 100.0;
    private final Clock clock;
    private final Map<CustomFuture<?>, Long> lastRunStartTimes = new ConcurrentHashMap<>();
    private final DistributionSummary discrepancySummary;
    private final Set<CustomFuture<?>> running = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Counter processingTimeCounter;
    private final Counter failedJobsCounter;
    private final Counter succeededJobsCounter;

    public MonitoredScheduledThreadPoolExecutor(final int corePoolSize,
                                                @Nonnull final MeterRegistry registry,
                                                final String... tags) {
        this(corePoolSize, Clock.getDefault(), registry, tags);
    }

    public MonitoredScheduledThreadPoolExecutor(final int corePoolSize,
                                                @Nonnull final Clock clock,
                                                @Nonnull final MeterRegistry registry,
                                                final String... tags) {
        super(corePoolSize);
        this.clock = clock;
        discrepancySummary = DistributionSummary.builder("scheduler.discrepancy")
                .baseUnit("percentage")
                .publishPercentileHistogram()
                .tags(tags)
                .register(registry);
        processingTimeCounter = Counter.builder("scheduler.processing.time")
                .baseUnit("nanoseconds")
                .tags(tags)
                .register(registry);
        failedJobsCounter = Counter.builder("scheduler.failed.jobs")
                .baseUnit("jobs")
                .tags(tags)
                .register(registry);
        succeededJobsCounter = Counter.builder("scheduler.succeeded.jobs")
                .baseUnit("jobs")
                .tags(tags)
                .register(registry);
    }

    @Override
    @Nonnull
    public ScheduledFuture<?> scheduleAtFixedRate(final Runnable command,
                                                  final long initialDelay,
                                                  final long period,
                                                  final TimeUnit unit) {
        CustomFuture<?> future = (CustomFuture<?>) super.scheduleAtFixedRate(command, initialDelay, period, unit);
        future.setPeriodInNanos(unit.toNanos(period));
        return future;
    }

    @Override
    protected <V> RunnableScheduledFuture<V> decorateTask(final Runnable runnable,
                                                          final RunnableScheduledFuture<V> task) {
        return new CustomFuture<>(super.decorateTask(runnable, task));
    }

    @Override
    protected void beforeExecute(final Thread t, final Runnable r) {
        super.beforeExecute(t, r);
        if (r instanceof CustomFuture) {
            CustomFuture<?> future = (CustomFuture<?>) r;
            Long lastTime = lastRunStartTimes.get(future);
            long now = clock.monotonicTime();
            if (lastTime != null) {
                long period = future.getPeriodInNanos();
                if (period != 0) {
                    double discrepancy = ONE_HUNDRED_PERCENTS * (Math.abs(now - lastTime - period)) / period;
                    discrepancySummary.record(discrepancy);
                }
            }
            lastRunStartTimes.put(future, now);
            running.add(future);
        }
    }

    @Override
    protected void afterExecute(final Runnable runnable, final Throwable throwable) {
        super.afterExecute(runnable, throwable);
        Throwable finalThrowable = throwable;
        if (runnable instanceof CustomFuture) {
            CustomFuture<?> future = (CustomFuture<?>) runnable;
            long processingTime = clock.monotonicTime() - lastRunStartTimes.get(future);
            processingTimeCounter.increment(processingTime);
            running.remove(future);
            if (throwable == null && future.isDone()) {
                try {
                    ((Future) runnable).get();
                } catch (CancellationException ce) {
                    finalThrowable = ce;
                } catch (ExecutionException ee) {
                    finalThrowable = ee.getCause();
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
            if (finalThrowable != null) {
                failedJobsCounter.increment();
            } else {
                succeededJobsCounter.increment();
            }
        }
    }

}
