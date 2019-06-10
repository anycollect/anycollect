package io.github.anycollect.core.impl.scheduler;

import io.github.anycollect.metric.Counter;
import io.github.anycollect.metric.MeterRegistry;
import io.github.anycollect.metric.Tags;

public final class SkippingOverworkRunnable implements Runnable {
    private final Runnable delegate;
    private final long periodInNanos;
    private volatile long lastStartTime;
    private final Counter rejectedJobsCounter;

    public SkippingOverworkRunnable(final Runnable delegate,
                                    final long periodInNanos,
                                    final MeterRegistry registry,
                                    final String prefix,
                                    final Tags tags) {
        this.delegate = delegate;
        this.periodInNanos = periodInNanos;
        this.rejectedJobsCounter = Counter.key(prefix, "scheduler.jobs.rejected")
                .concatTags(tags)
                .meta(this.getClass())
                .register(registry);
    }

    @Override
    public void run() {
        if (System.nanoTime() - lastStartTime < periodInNanos) {
            rejectedJobsCounter.increment();
        } else {
            lastStartTime = System.nanoTime();
            this.delegate.run();
        }
    }
}
