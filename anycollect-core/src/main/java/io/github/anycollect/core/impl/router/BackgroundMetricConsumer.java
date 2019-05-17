package io.github.anycollect.core.impl.router;

import io.github.anycollect.metric.Counter;
import io.github.anycollect.metric.Gauge;
import io.github.anycollect.metric.MeterRegistry;
import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

@ThreadSafe
public final class BackgroundMetricConsumer implements MetricConsumer {
    private final ExecutorService executor;
    private final MetricConsumer delegate;
    private volatile boolean stopped = false;
    private final AtomicLong inputSize = new AtomicLong(0L);
    private final Counter rejected;

    public BackgroundMetricConsumer(@Nonnull final ExecutorService executor,
                                    @Nonnull final MetricConsumer delegate,
                                    @Nonnull final MeterRegistry registry) {
        this.executor = executor;
        this.delegate = delegate;
        Gauge.make("router.route.input.size", inputSize, AtomicLong::get)
                .unit("metrics")
                .tag("route", getAddress())
                .meta(this.getClass())
                .register(registry);
        this.rejected = Counter.key("router.route.rejected")
                .unit("metrics")
                .tag("route", getAddress())
                .meta(this.getClass())
                .register(registry);
        if (executor instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor threadPool = (ThreadPoolExecutor) executor;
            Gauge.make("router.route.queue.size", threadPool, pool -> pool.getQueue().size())
                    .tag("route", getAddress())
                    .meta(this.getClass())
                    .register(registry);
        }
    }

    @Override
    public void consume(@Nonnull final List<? extends Metric> metrics) {
        if (!stopped) {
            try {
                inputSize.addAndGet(metrics.size());
                executor.submit(() -> {
                    inputSize.getAndAdd(-metrics.size());
                    delegate.consume(metrics);
                });
            } catch (RejectedExecutionException e) {
                rejected.increment(metrics.size());
            }
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
