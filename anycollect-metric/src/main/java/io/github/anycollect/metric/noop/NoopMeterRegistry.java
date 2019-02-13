package io.github.anycollect.metric.noop;

import io.github.anycollect.metric.Counter;
import io.github.anycollect.metric.DistributionSummary;
import io.github.anycollect.metric.MeterId;
import io.github.anycollect.metric.MeterRegistry;

import javax.annotation.Nonnull;

public final class NoopMeterRegistry implements MeterRegistry {
    @Override
    public Counter counter(@Nonnull final MeterId id) {
        return new NoopCounter(id);
    }

    @Override
    public DistributionSummary summary(@Nonnull final MeterId id, @Nonnull final double... percentiles) {
        return new NoopDistributionSummary(id);
    }

    @Override
    public DistributionSummary summary(@Nonnull final MeterId id) {
        return new NoopDistributionSummary(id);
    }
}
