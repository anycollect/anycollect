package io.github.anycollect.core.impl.scheduler;

import io.github.anycollect.metric.Counter;
import io.github.anycollect.metric.MeterRegistry;
import io.github.anycollect.metric.Tags;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public final class ThrottledRunnable implements Runnable {
    private final Runnable delegate;
    private final long periodInNanos;
    private final Counter throttledJobsCounter;
    private volatile Delayed delayed;

    public ThrottledRunnable(final Runnable delegate,
                             final long periodInNanos,
                             final MeterRegistry registry,
                             final String prefix,
                             final Tags tags) {
        this.delegate = delegate;
        this.periodInNanos = periodInNanos;
        this.throttledJobsCounter = Counter.key(prefix, "scheduler.jobs.throttled")
                .concatTags(tags)
                .meta(this.getClass())
                .register(registry);
    }

    void setDelayed(final Delayed delayed) {
        this.delayed = delayed;
    }

    @Override
    public void run() {
        if (delayed == null) {
            this.delegate.run();
        } else {
            long delay = delayed.getDelay(TimeUnit.NANOSECONDS);
            // throttle obsolete calls
            if (-1 * delay > periodInNanos) {
                throttledJobsCounter.increment();
            } else {
                this.delegate.run();
            }
        }
    }
}
