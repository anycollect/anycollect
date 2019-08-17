package io.github.anycollect.meter.api;

import io.github.anycollect.metric.Key;

import javax.annotation.Nonnull;

abstract class BaseMeterBuilder<T extends BaseMeterBuilder<T>> extends BaseBuilder<T> {
    BaseMeterBuilder(@Nonnull final Key key) {
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
