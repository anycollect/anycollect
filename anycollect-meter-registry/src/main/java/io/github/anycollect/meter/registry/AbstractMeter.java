package io.github.anycollect.meter.registry;

import io.github.anycollect.metric.MeterId;

import javax.annotation.Nonnull;

public abstract class AbstractMeter implements Meter {
    private final MeterId id;

    public AbstractMeter(@Nonnull final MeterId id) {
        this.id = id;
    }

    @Nonnull
    public final MeterId getId() {
        return id;
    }
}
