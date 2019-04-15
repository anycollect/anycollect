package io.github.anycollect.extensions.scope;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class SimpleScope extends AbstractScope {
    private final String id;

    public SimpleScope(@Nullable final Scope parent, @Nonnull final String id) {
        super(parent);
        this.id = id;
    }

    @Nonnull
    @Override
    public String getId() {
        return id;
    }
}
