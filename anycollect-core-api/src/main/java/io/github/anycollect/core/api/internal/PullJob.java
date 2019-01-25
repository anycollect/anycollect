package io.github.anycollect.core.api.internal;

import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.Optional;

public interface PullJob<T extends Target, Q extends Query> {
    @Nonnull
    T getTarget();

    @Nonnull
    Q getQuery();

    @Nonnull
    Result run();

    @Nonnull
    default Optional<Duration> getRepeatInterval() {
        return Optional.empty();
    }
}
