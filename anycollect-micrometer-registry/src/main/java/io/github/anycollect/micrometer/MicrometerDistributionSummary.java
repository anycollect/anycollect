package io.github.anycollect.micrometer;

import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.metric.Distribution;
import io.github.anycollect.metric.Measurement;
import io.github.anycollect.metric.MeterId;
import io.github.anycollect.metric.MetricFamily;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public final class MicrometerDistributionSummary implements DistributionSummary, MeterAdapter {
    private final DistributionSummary delegate;
    private final Distribution adapter;
    private final MeterId id;
    private final Clock clock;
    private final List<SnapshotMetric> metrics;

    public MicrometerDistributionSummary(
            @Nonnull final DistributionSummary delegate,
            @Nonnull final MeterId id,
            @Nonnull final Clock clock) {
        this.delegate = delegate;
        this.adapter = new AnyCollectAdapter();
        this.id = id;
        this.clock = clock;

        HistogramSnapshot initialSnapshot = takeSnapshot();
        ValueAtPercentile[] valueAtPercentiles = initialSnapshot.percentileValues();
        this.metrics = new ArrayList<>(valueAtPercentiles.length + 2);
        for (int i = 0; i < valueAtPercentiles.length; i++) {
            this.metrics.add(new Percentile(initialSnapshot.percentileValues()[i].percentile(), i));
        }
        this.metrics.add(new MeanMetric());
        this.metrics.add(new MaxMetric());
    }

    @Override
    public void record(final double amount) {
        delegate.record(amount);
    }

    @Override
    public long count() {
        return delegate.count();
    }

    @Override
    public double totalAmount() {
        return delegate.totalAmount();
    }

    @Override
    public double max() {
        return delegate.max();
    }

    @Override
    public HistogramSnapshot takeSnapshot() {
        return delegate.takeSnapshot();
    }

    @Override
    public Id getId() {
        return delegate.getId();
    }

    @Override
    public Distribution getMeter() {
        return adapter;
    }

    private class AnyCollectAdapter implements Distribution {
        @Override
        public void record(final double amount) {
            MicrometerDistributionSummary.this.record(amount);
        }

        @Nonnull
        @Override
        public MeterId getId() {
            return MicrometerDistributionSummary.this.id;
        }

        @Nonnull
        @Override
        public MetricFamily measure() {
            long now = clock.wallTime();
            HistogramSnapshot snapshot = takeSnapshot();
            List<Measurement> measurements = metrics.stream()
                    .map(metric -> metric.measure(snapshot, now)).collect(toList());
            return MetricFamily.of(getId(), measurements, clock.wallTime());
        }
    }

    private interface SnapshotMetric {
        Measurement measure(HistogramSnapshot snapshot, long timestamp);
    }

    private class MaxMetric implements SnapshotMetric {
        @Override
        public Measurement measure(final HistogramSnapshot snapshot, final long timestamp) {
            return Measurement.max(snapshot.max(), id.getUnit());
        }
    }

    private class MeanMetric implements SnapshotMetric {
        @Override
        public Measurement measure(final HistogramSnapshot snapshot, final long timestamp) {
            return Measurement.mean(snapshot.mean(), id.getUnit());
        }
    }

    private class Percentile implements SnapshotMetric {
        private final double percentile;
        private final int index;

        Percentile(final double percentile, final int index) {
            this.percentile = percentile;
            this.index = index;
        }

        double getValue(final HistogramSnapshot snapshot) {
            return snapshot.percentileValues()[index].value();
        }

        @Override
        public Measurement measure(final HistogramSnapshot snapshot, final long timestamp) {
            return Measurement.percentile(percentile, getValue(snapshot), id.getUnit());
        }
    }
}
