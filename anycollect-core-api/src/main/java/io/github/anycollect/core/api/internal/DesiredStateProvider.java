package io.github.anycollect.core.api.internal;

import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.core.api.common.Plugin;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;

import javax.annotation.Nonnull;

public interface DesiredStateProvider<T extends Target<Q>, Q extends Query> extends Plugin, Lifecycle {
    @Nonnull
    State<T, Q> current();
}
