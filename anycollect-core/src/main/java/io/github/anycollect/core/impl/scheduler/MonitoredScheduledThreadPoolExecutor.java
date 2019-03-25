package io.github.anycollect.core.impl.scheduler;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.metric.*;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

public final class MonitoredScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {
    private static final double ONE_HUNDRED_PERCENTS = 100.0;
    private final Clock clock;
    private final Map<CustomFuture<?>, Long> lastRunStartTimes = new ConcurrentHashMap<>();
    private final Distribution discrepancySummary;
    private final Set<CustomFuture<?>> running = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Timer processingTimeSummary;
    private final Counter failedJobsCounter;
    private final Counter succeededJobsCounter;

    public MonitoredScheduledThreadPoolExecutor(final int corePoolSize,
                                                @Nonnull final MeterRegistry registry,
                                                @Nonnull final String prefix,
                                                @Nonnull final Tags tags) {
        this(corePoolSize, new ThreadFactoryBuilder().setNameFormat("thread-[%d]").build(), registry, prefix, tags);
    }

    public MonitoredScheduledThreadPoolExecutor(final int corePoolSize,
                                                final ThreadFactory threadFactory,
                                                @Nonnull final MeterRegistry registry,
                                                @Nonnull final String prefix,
                                                @Nonnull final Tags tags) {
        super(corePoolSize, threadFactory);
        this.clock = Clock.getDefault();
        discrepancySummary = Distribution.key(prefix, "scheduler.discrepancy")
                .unit("percents")
                .concatTags(tags)
                .meta(this.getClass())
                .register(registry);
        processingTimeSummary = Timer.key(prefix, "scheduler.processing.time")
                .unit(TimeUnit.MILLISECONDS)
                .concatTags(tags)
                .meta(this.getClass())
                .register(registry);
        failedJobsCounter = Counter.key(prefix, "scheduler.jobs.failed")
                .concatTags(tags)
                .meta(this.getClass())
                .register(registry);
        succeededJobsCounter = Counter.key(prefix, "scheduler.jobs.succeeded")
                .concatTags(tags)
                .meta(this.getClass())
                .register(registry);
        Gauge.make(this, executor -> executor.getQueue().size(), prefix, "scheduler.queue.size")
                .concatTags(tags)
                .register(registry);
        Gauge.make(this, executor -> getPoolSize(), prefix, "scheduler.threads.live");
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
                    int discrepancy = (int) (ONE_HUNDRED_PERCENTS * (Math.abs(now - lastTime - period)) / period);
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
            processingTimeSummary.record(processingTime, TimeUnit.NANOSECONDS);
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
