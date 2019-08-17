package io.github.anycollect.meter.impl;

import io.github.anycollect.meter.api.MeterId;

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
