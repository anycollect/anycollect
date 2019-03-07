package io.github.anycollect.core.api.target;

import javax.annotation.Nonnull;

public interface TargetFactory<D, T> {
    @Nonnull
    T create(@Nonnull D definition) throws TargetCreationException;
}
