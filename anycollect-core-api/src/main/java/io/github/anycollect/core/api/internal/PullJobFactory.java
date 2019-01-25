package io.github.anycollect.core.api.internal;

import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;

import javax.annotation.Nonnull;
import java.util.Optional;

public interface PullJobFactory<T extends Target, Q extends Query> {
    @Nonnull
    Optional<PullJob<T, Q>> create(@Nonnull T target, @Nonnull Q query);
}
