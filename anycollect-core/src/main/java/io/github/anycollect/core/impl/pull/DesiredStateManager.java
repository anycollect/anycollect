package io.github.anycollect.core.impl.pull;

import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.core.api.internal.State;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;

import javax.annotation.Nonnull;

public interface DesiredStateManager<T extends Target<Q>, Q extends Query> extends Lifecycle {
    void update(@Nonnull State<T, Q> desiredState);

    void cleanup();
}
