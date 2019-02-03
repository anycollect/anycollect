package io.github.anycollect.core.impl;

import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;

import javax.annotation.Nonnull;

public interface Puller {
    <T extends Target<Q>, Q extends Query> void pullAsync(@Nonnull PullJob<T, Q> job);
}
