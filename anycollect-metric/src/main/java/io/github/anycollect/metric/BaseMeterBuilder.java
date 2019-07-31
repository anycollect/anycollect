package io.github.anycollect.metric;

import javax.annotation.Nonnull;

public abstract class BaseMeterBuilder<T extends BaseMeterBuilder<T>> extends BaseBuilder<T> {
    public BaseMeterBuilder(@Nonnull final Key key) {
        key(key);
    }

    /**
     * Adds source class meta tag
     *
     * @param source - class that creates and uses meter
     * @return current builder
     */
    public T meta(@Nonnull final Class<?> source) {
        return super.meta("class", source.getName());
    }
}
