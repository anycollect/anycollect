package io.github.anycollect.micrometer;

import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.metric.Distribution;
import io.github.anycollect.metric.MeterId;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.MetricId;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class MicrometerDistributionSummary implements DistributionSummary {
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
        MetricId meanId = id.mean();
        MetricId maxId = id.max();

        HistogramSnapshot initialSnapshot = takeSnapshot();
        ValueAtPercentile[] valueAtPercentiles = initialSnapshot.percentileValues();
        this.metrics = new ArrayList<>(valueAtPercentiles.length + 2);
        for (int i = 0; i < valueAtPercentiles.length; i++) {
            MetricId percentileId = id.percentile(initialSnapshot.percentileValues()[i].percentile());
            this.metrics.add(new Percentile(percentileId, i));
        }
        this.metrics.add(new MeanMetric(meanId));
        this.metrics.add(new MaxMetric(maxId));
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

    public Distribution getAdapter() {
        return adapter;
    }

    private class AnyCollectAdapter implements Distribution {
        @Override
        public void record(final double amount) {
            MicrometerDistributionSummary.this.record(amount);
        }

        @Override
        public MeterId getId() {
            return MicrometerDistributionSummary.this.id;
        }

        @Override
        public Stream<Metric> measure() {
            long now = clock.wallTime();
            HistogramSnapshot snapshot = takeSnapshot();
            return metrics.stream().map(metric -> metric.toMetric(snapshot, now));
        }
    }

    private interface SnapshotMetric {
        Metric toMetric(HistogramSnapshot snapshot, long timestamp);
    }

    private static class MaxMetric implements SnapshotMetric {
        private final MetricId id;

        MaxMetric(final MetricId id) {
            this.id = id;
        }

        @Override
        public Metric toMetric(final HistogramSnapshot snapshot, final long timestamp) {
            return Metric.of(id, snapshot.max(), timestamp);
        }
    }

    private static class MeanMetric implements SnapshotMetric {
        private final MetricId id;

        MeanMetric(final MetricId id) {
            this.id = id;
        }

        @Override
        public Metric toMetric(final HistogramSnapshot snapshot, final long timestamp) {
            return Metric.of(id, snapshot.mean(), timestamp);
        }
    }

    private static class Percentile implements SnapshotMetric {
        private final MetricId id;
        private final int index;

        Percentile(final MetricId id, final int index) {
            this.id = id;
            this.index = index;
        }

        double getValue(final HistogramSnapshot snapshot) {
            return snapshot.percentileValues()[index].value();
        }

        @Override
        public Metric toMetric(final HistogramSnapshot snapshot, final long timestamp) {
            return Metric.of(id, getValue(snapshot), timestamp);
        }
    }
}
