package io.github.anycollect.core.api.query;

import lombok.EqualsAndHashCode;

import javax.annotation.Nonnull;

@EqualsAndHashCode
public abstract class AbstractQuery implements Query {
    private final String id;

    public AbstractQuery(@Nonnull final String id) {
        this.id = id;
    }

    @Nonnull
    @Override
    public final String getId() {
        return id;
    }
}
