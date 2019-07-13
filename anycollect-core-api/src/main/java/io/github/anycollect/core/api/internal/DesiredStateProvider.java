package io.github.anycollect.core.api.internal;

import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;

import javax.annotation.Nonnull;

public interface DesiredStateProvider<T extends Target, Q extends Query<T>> {
    @Nonnull
    State<T, Q> current();
}
