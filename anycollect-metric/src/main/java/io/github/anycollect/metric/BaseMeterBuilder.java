package io.github.anycollect.metric;

import javax.annotation.Nonnull;

public abstract class BaseMeterBuilder<T extends BaseMeterBuilder<T>> extends BaseBuilder<T> {
    public BaseMeterBuilder(@Nonnull final String key) {
        key(key);
    }

    public BaseMeterBuilder(@Nonnull final String... keyParts) {
        key(keyParts);
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
