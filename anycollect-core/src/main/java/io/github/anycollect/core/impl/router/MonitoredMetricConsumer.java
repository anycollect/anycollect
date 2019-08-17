package io.github.anycollect.core.impl.router;

import io.github.anycollect.meter.api.Counter;
import io.github.anycollect.meter.api.MeterRegistry;
import io.github.anycollect.metric.Sample;
import io.github.anycollect.meter.api.Timer;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class MonitoredMetricConsumer implements MetricConsumer {
    private final MetricConsumer delegate;
    private final Counter consumedMetrics;
    private final Timer processingTime;

    public MonitoredMetricConsumer(@Nonnull final MetricConsumer delegate,
                                   @Nonnull final MeterRegistry registry) {
        this.delegate = delegate;
        this.consumedMetrics = Counter.key("router/route/delivery")
                .unit("metrics")
                .tag("route", getAddress())
                .meta(this.getClass())
                .register(registry);
        this.processingTime = Timer.key("router/route/processing.time")
                .unit(TimeUnit.MILLISECONDS)
                .tag("route", getAddress())
                .meta(this.getClass())
                .register(registry);
    }

    @Override
    public void consume(@Nonnull final List<? extends Sample> samples) {
        consumedMetrics.increment(samples.size());
        processingTime.record(() -> delegate.consume(samples));
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
