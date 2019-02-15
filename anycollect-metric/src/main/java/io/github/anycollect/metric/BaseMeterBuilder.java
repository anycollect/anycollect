package io.github.anycollect.metric;

import javax.annotation.Nonnull;

public abstract class BaseMeterBuilder<T extends BaseMeterBuilder<T>> extends BaseBuilder<T> {
    public BaseMeterBuilder(@Nonnull final String value) {
        key(value);
    }

    @Override
    public T unit(@Nonnull final String value) {
        return super.unit(value);
    }

    @Override
    public T nanos() {
        return super.nanos();
    }
}
