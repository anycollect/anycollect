package io.github.anycollect.metric.noop;

import io.github.anycollect.metric.DistributionSummary;
import io.github.anycollect.metric.MeterId;

import javax.annotation.Nonnull;

public final class NoopDistributionSummary extends NoopAbstractMeter implements DistributionSummary {
    public NoopDistributionSummary(@Nonnull final MeterId id) {
        super(id);
    }

    @Override
    public void record(final double amount) {
    }
}
