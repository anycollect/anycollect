package io.github.anycollect.core.impl.router;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.meter.api.Counter;
import io.github.anycollect.meter.api.MeterRegistry;
import io.github.anycollect.metric.Sample;

import javax.annotation.Nonnull;
import java.util.List;

public final class MonitoredMetricProducer implements MetricProducer {
    private final MetricProducer delegate;
    private final MeterRegistry registry;

    public MonitoredMetricProducer(@Nonnull final MetricProducer delegate, @Nonnull final MeterRegistry registry) {
        this.delegate = delegate;
        this.registry = registry;
    }

    @Override
    public void start(@Nonnull final Dispatcher dispatcher) {
        MonitoredDispatcher monitoredDispatcher = new MonitoredDispatcher(dispatcher, registry, this);
        delegate.start(monitoredDispatcher);
    }

    @Nonnull
    @Override
    public String getAddress() {
        return delegate.getAddress();
    }

    @Override
    public String toString() {
        return delegate.getAddress();
    }

    private static final class MonitoredDispatcher implements Dispatcher {
        private final Dispatcher delegate;
        private final Counter producedMetrics;

        MonitoredDispatcher(@Nonnull final Dispatcher delegate,
                            @Nonnull final MeterRegistry registry,
                            @Nonnull final MetricProducer producer) {
            this.delegate = delegate;
            this.producedMetrics = Counter.key("router/route/dispatch")
                    .unit("metrics")
                    .tag("route", producer.getAddress())
                    .meta(this.getClass())
                    .register(registry);
        }

        @Override
        public void dispatch(@Nonnull final Sample sample) {
            producedMetrics.increment();
            delegate.dispatch(sample);
        }

        @Override
        public void dispatch(@Nonnull final List<Sample> samples) {
            producedMetrics.increment(samples.size());
            delegate.dispatch(samples);
        }
    }
}
