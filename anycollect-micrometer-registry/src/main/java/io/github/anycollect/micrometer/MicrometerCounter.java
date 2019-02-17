package io.github.anycollect.micrometer;

import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.metric.Measurement;
import io.github.anycollect.metric.MeterId;
import io.github.anycollect.metric.MetricFamily;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;

import javax.annotation.Nonnull;

public final class MicrometerCounter implements Counter, MeterAdapter {
    private final Clock clock;
    private final Counter delegate;
    private final MeterId id;
    private final AnyCollectAdapter adapter;

    public MicrometerCounter(final Clock clock, final Counter delegate, final MeterId id) {
        this.clock = clock;
        this.delegate = delegate;
        this.id = id;
        adapter = new AnyCollectAdapter();
    }

    @Override
    public void increment() {
        delegate.increment();
    }

    @Override
    public void increment(final double amount) {
        delegate.increment(amount);
    }

    @Override
    public double count() {
        return delegate.count();
    }

    @Override
    public Meter.Id getId() {
        return delegate.getId();
    }

    @Override
    public io.github.anycollect.metric.Counter getMeter() {
        return adapter;
    }

    private class AnyCollectAdapter implements io.github.anycollect.metric.Counter {
        @Override
        public void increment(final double amount) {
            MicrometerCounter.this.increment(amount);
        }

        @Nonnull
        @Override
        public MeterId getId() {
            return id;
        }

        @Nonnull
        @Override
        public MetricFamily measure() {
            return MetricFamily.of(getId(), Measurement.counter(count(), id.getUnit()), clock.wallTime());
        }
    }
}
