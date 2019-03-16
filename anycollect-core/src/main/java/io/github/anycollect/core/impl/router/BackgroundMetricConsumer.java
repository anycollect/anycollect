package io.github.anycollect.core.impl.router;

import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;
import java.util.concurrent.ExecutorService;

@ThreadSafe
public final class BackgroundMetricConsumer implements MetricConsumer {
    private final ExecutorService executor;
    private final MetricConsumer delegate;
    private volatile boolean stopped = false;

    public BackgroundMetricConsumer(@Nonnull final ExecutorService executor,
                                    @Nonnull final MetricConsumer delegate) {
        this.executor = executor;
        this.delegate = delegate;
    }

    @Override
    public void consume(@Nonnull final List<? extends Metric> metrics) {
        if (!stopped) {
            executor.submit(() -> delegate.consume(metrics));
        }
    }

    @Override
    public void stop() {
        stopped = true;
        delegate.stop();
        executor.shutdownNow();
    }

    @Override
    public String toString() {
        return delegate.getAddress();
    }

    @Nonnull
    @Override
    public String getAddress() {
        return delegate.getAddress();
    }
}
