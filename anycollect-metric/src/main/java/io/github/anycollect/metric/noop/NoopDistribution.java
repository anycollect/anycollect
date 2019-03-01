package io.github.anycollect.metric.noop;

import io.github.anycollect.metric.Distribution;
import io.github.anycollect.metric.MeterId;

import javax.annotation.Nonnull;

public final class NoopDistribution extends NoopAbstractMeter implements Distribution {
    public NoopDistribution(@Nonnull final MeterId id) {
        super(id);
    }

    @Override
    public void record(final long amount) {
    }
}
