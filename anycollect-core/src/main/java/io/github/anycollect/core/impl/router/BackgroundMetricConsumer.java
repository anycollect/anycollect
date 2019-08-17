package io.github.anycollect.core.impl.router;

import io.github.anycollect.meter.api.Counter;
import io.github.anycollect.meter.api.Gauge;
import io.github.anycollect.meter.api.MeterRegistry;
import io.github.anycollect.metric.Sample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

@ThreadSafe
public final class BackgroundMetricConsumer implements MetricConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(BackgroundMetricConsumer.class);
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
    public void consume(@Nonnull final List<? extends Sample> samples) {
        if (!stopped) {
            try {
                inputSize.addAndGet(samples.size());
                executor.submit(() -> {
                    inputSize.getAndAdd(-samples.size());
                    delegate.consume(samples);
                });
            } catch (RejectedExecutionException e) {
                rejected.increment(samples.size());
            }
        }
    }

    @Override
    public void stop() {
        stopped = true;
        delegate.stop();
        LOG.info("Stopping async input queue workers for {}, there are currently {} unprocessed metrics",
                getAddress(), inputSize.get());
        this.executor.shutdown();
        LOG.info("Input queue worker for {} has been successfully stopped", getAddress());
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
