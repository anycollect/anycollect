package io.github.anycollect.core.impl.pull.availability;

import io.github.anycollect.core.api.internal.State;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;

import javax.annotation.Nonnull;

public interface HealthChecker<T extends Target<Q>, Q extends Query> {
    void stop();

    void update(@Nonnull State<T, Q> state);
}
