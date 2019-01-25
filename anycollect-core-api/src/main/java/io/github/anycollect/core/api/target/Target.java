package io.github.anycollect.core.api.target;

import javax.annotation.Nonnull;

public interface Target {
    @Nonnull
    Id getId();

    interface Id {
    }
}
