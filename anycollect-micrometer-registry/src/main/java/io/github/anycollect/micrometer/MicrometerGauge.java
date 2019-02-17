package io.github.anycollect.micrometer;

import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.metric.Measurement;
import io.github.anycollect.metric.MeterId;
import io.github.anycollect.metric.MetricFamily;
import io.micrometer.core.instrument.Gauge;

import javax.annotation.Nonnull;

public final class MicrometerGauge implements Gauge, MeterAdapter {
    private final Clock clock;
    private final Gauge delegate;
    private final MeterId meterId;
    private final AnyCollectAdapter adapter;

    public MicrometerGauge(final Clock clock, final Gauge delegate, final MeterId meterId) {
        this.clock = clock;
        this.delegate = delegate;
        this.meterId = meterId;
        adapter = new AnyCollectAdapter();
    }

    @Override
    public double value() {
        return delegate.value();
    }

    @Override
    public Id getId() {
        return delegate.getId();
    }

    @Override
    public io.github.anycollect.metric.Gauge getMeter() {
        return adapter;
    }

    private class AnyCollectAdapter implements io.github.anycollect.metric.Gauge {
        @Nonnull
        @Override
        public MeterId getId() {
            return meterId;
        }

        @Nonnull
        @Override
        public MetricFamily measure() {
            return MetricFamily.of(getId(), Measurement.gauge(value()), clock.wallTime());
        }
    }
}
