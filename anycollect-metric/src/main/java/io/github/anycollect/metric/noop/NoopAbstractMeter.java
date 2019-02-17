package io.github.anycollect.metric.noop;

import io.github.anycollect.metric.Meter;
import io.github.anycollect.metric.MeterId;
import io.github.anycollect.metric.MetricFamily;
import lombok.EqualsAndHashCode;

import javax.annotation.Nonnull;

@EqualsAndHashCode(of = "id")
public class NoopAbstractMeter implements Meter {
    private final MeterId id;

    public NoopAbstractMeter(@Nonnull final MeterId id) {
        this.id = id;
    }

    @Nonnull
    @Override
    public final MeterId getId() {
        return id;
    }

    @Nonnull
    @Override
    public final MetricFamily measure() {
        return MetricFamily.empty(id, System.currentTimeMillis());
    }
}
