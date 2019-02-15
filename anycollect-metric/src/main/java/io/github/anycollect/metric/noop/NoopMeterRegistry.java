package io.github.anycollect.metric.noop;

import io.github.anycollect.metric.Counter;
import io.github.anycollect.metric.Distribution;
import io.github.anycollect.metric.MeterId;
import io.github.anycollect.metric.MeterRegistry;

import javax.annotation.Nonnull;

public final class NoopMeterRegistry implements MeterRegistry {
    @Override
    public Counter counter(@Nonnull final MeterId id) {
        return new NoopCounter(id);
    }

    @Override
    public Distribution distribution(@Nonnull final MeterId id) {
        return new NoopDistribution(id);
    }
}
