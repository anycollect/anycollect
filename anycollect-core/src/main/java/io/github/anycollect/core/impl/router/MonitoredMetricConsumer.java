package io.github.anycollect.core.impl.router;

import io.github.anycollect.metric.*;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MonitoredMetricConsumer implements MetricConsumer {
    private final MetricConsumer delegate;
    private final Counter consumedMetrics;
    private final Timer processingTime;

    public MonitoredMetricConsumer(@Nonnull final MetricConsumer delegate,
                                   @Nonnull final MeterRegistry registry) {
        this.delegate = delegate;
        this.consumedMetrics = Counter.key("router.route.delivery")
                .unit("metrics")
                .tag("route", getAddress())
                .register(registry);
        this.processingTime = Timer.key("router.route.processing.time")
                .unit(TimeUnit.MILLISECONDS)
                .tag("route", getAddress())
                .register(registry);
    }

    @Override
    public void consume(@Nonnull final List<? extends Metric> metrics) {
        consumedMetrics.increment(metrics.size());
        processingTime.record(() -> delegate.consume(metrics));
        delegate.consume(metrics);
    }

    @Override
    public void stop() {
        delegate.stop();
    }

    @Nonnull
    @Override
    public String getAddress() {
        return delegate.getAddress();
    }
}
