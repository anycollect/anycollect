package io.github.anycollect.extensions.scope;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Scope {
    @Nullable
    Scope getParent();

    @Nonnull
    String getId();

    boolean isParent(@Nonnull Scope that);

    int distance(@Nonnull Scope that);
}
