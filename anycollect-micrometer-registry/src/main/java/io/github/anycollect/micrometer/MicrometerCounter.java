package io.github.anycollect.micrometer;

import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.metric.MeterId;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.MetricId;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;

import java.util.stream.Stream;

public final class MicrometerCounter implements Counter {
    private final Counter delegate;
    private final MeterId id;
    private final MetricId counterMetricId;
    private final Clock clock;
    private final AnyCollectAdapter adapter;

    public MicrometerCounter(final Counter delegate, final MeterId id, final Clock clock) {
        this.delegate = delegate;
        this.id = id;
        this.clock = clock;
        counterMetricId = id.counter();
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

    public io.github.anycollect.metric.Counter getAdapter() {
        return adapter;
    }

    private class AnyCollectAdapter implements io.github.anycollect.metric.Counter {
        @Override
        public double count() {
            return MicrometerCounter.this.count();
        }

        @Override
        public void increment(final double amount) {
            MicrometerCounter.this.increment(amount);
        }

        @Override
        public MeterId getId() {
            return id;
        }

        @Override
        public Stream<Metric> measure() {
            return Stream.of(Metric.of(counterMetricId, count(), clock.wallTime()));
        }
    }
}
