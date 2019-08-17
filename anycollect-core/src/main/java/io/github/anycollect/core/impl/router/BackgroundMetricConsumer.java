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
import java.util.concurrent.atomic.AtomicLong;

@ThreadSafe
public final class BackgroundMetricConsumer implements MetricConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(BackgroundMetricConsumer.class);
    private final ExecutorService executor;
    private final MetricConsumer delegate;
    private volatile boolean stopped = false;
    private final AtomicLong awaitingMetrics = new AtomicLong(0L);
    private final Counter rejected;

    public BackgroundMetricConsumer(@Nonnull final ExecutorService executor,
                                    @Nonnull final MetricConsumer delegate,
                                    @Nonnull final MeterRegistry registry) {
        this.executor = executor;
        this.delegate = delegate;
        Gauge.make("router/route/awaiting.metrics", awaitingMetrics, AtomicLong::get)
                .tag("route", getAddress())
                .meta(this.getClass())
                .register(registry);
        this.rejected = Counter.key("router/route/rejected.metrics")
                .tag("route", getAddress())
                .meta(this.getClass())
                .register(registry);
    }

    @Override
    public void consume(@Nonnull final List<? extends Sample> samples) {
        if (!stopped) {
            try {
                awaitingMetrics.addAndGet(samples.size());
                executor.submit(() -> {
                    awaitingMetrics.getAndAdd(-samples.size());
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
                getAddress(), awaitingMetrics.get());
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
