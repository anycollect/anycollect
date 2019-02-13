package io.github.anycollect.metric.noop;

import io.github.anycollect.metric.Counter;
import io.github.anycollect.metric.MeterId;

import javax.annotation.Nonnull;

public final class NoopCounter extends NoopAbstractMeter implements Counter {
    public NoopCounter(@Nonnull final MeterId id) {
        super(id);
    }

    @Override
    public void increment(final double amount) {
    }
}
