package io.github.anycollect.metric;

import javax.annotation.Nonnull;

public abstract class BaseMeterBuilder<T extends BaseMeterBuilder<T>> extends BaseBuilder<T> {
    public BaseMeterBuilder(@Nonnull final String key) {
        key(key);
    }

    @Override
    public T unit(@Nonnull final String unit) {
        return super.unit(unit);
    }

    @Override
    public T nanos() {
        return super.nanos();
    }
}
