package io.github.anycollect.core.api.target;

import javax.annotation.Nonnull;

public interface TargetFactory<D, T> {
    @SuppressWarnings("rawtypes")
    TargetFactory IDENTITY = new TargetFactory() {
        @Nonnull
        @Override
        public Object create(@Nonnull final Object definition) {
            return definition;
        }
    };

    @SuppressWarnings("unchecked")
    static <T> TargetFactory<T, T> identity() {
        return (TargetFactory<T, T>) IDENTITY;
    }

    @Nonnull
    T create(@Nonnull D definition) throws TargetCreationException;
}
