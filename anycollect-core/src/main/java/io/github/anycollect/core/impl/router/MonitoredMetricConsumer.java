package io.github.anycollect.core.impl.router;

import io.github.anycollect.metric.Counter;
import io.github.anycollect.metric.MeterRegistry;
import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import java.util.List;

public class MonitoredMetricConsumer implements MetricConsumer {
    private final MetricConsumer delegate;
    private final Counter consumedMetrics;

    public MonitoredMetricConsumer(@Nonnull final MetricConsumer delegate,
                                   @Nonnull final MeterRegistry registry) {
        this.delegate = delegate;
        this.consumedMetrics = Counter.key("router.route.delivery")
                .unit("metrics")
                .tag("route", getAddress())
                .register(registry);
    }

    @Override
    public void consume(@Nonnull final List<? extends Metric> metrics) {
        consumedMetrics.increment(metrics.size());
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
