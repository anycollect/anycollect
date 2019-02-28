package io.github.anycollect.metric.noop;

import io.github.anycollect.metric.Gauge;
import io.github.anycollect.metric.MeterId;

import javax.annotation.Nonnull;

public final class NoopGauge extends NoopAbstractMeter implements Gauge {
    public NoopGauge(@Nonnull final MeterId id) {
        super(id);
    }
}
