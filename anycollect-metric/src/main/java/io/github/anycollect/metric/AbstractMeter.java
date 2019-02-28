package io.github.anycollect.metric;

import javax.annotation.Nonnull;

public abstract class AbstractMeter implements Meter {
    private final MeterId id;

    public AbstractMeter(@Nonnull final MeterId id) {
        this.id = id;
    }

    @Nonnull
    @Override
    public final MeterId getId() {
        return id;
    }
}
