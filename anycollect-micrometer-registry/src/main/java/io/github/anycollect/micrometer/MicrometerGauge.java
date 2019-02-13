package io.github.anycollect.micrometer;

import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.metric.MeterId;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.MetricId;
import io.micrometer.core.instrument.Gauge;

import java.util.stream.Stream;

public final class MicrometerGauge implements Gauge {
    private final Gauge delegate;
    private final Clock clock;
    private final MeterId meterId;
    private final MetricId gaugeId;
    private final AnyCollectAdapter adapter;

    public MicrometerGauge(final Gauge delegate, final MeterId meterId, final Clock clock) {
        this.delegate = delegate;
        this.clock = clock;
        this.meterId = meterId;
        this.gaugeId = meterId.value();
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

    public io.github.anycollect.metric.Gauge getAdapter() {
        return adapter;
    }

    private class AnyCollectAdapter implements io.github.anycollect.metric.Gauge {
        @Override
        public MeterId getId() {
            return meterId;
        }

        @Override
        public Stream<Metric> measure() {
            return Stream.of(Metric.of(gaugeId, value(), clock.wallTime()));
        }
    }
}
