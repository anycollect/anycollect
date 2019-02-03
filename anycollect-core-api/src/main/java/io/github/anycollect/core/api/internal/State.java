package io.github.anycollect.core.api.internal;

import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;

import javax.annotation.Nonnull;
import java.util.Set;

public interface State<T extends Target<Q>, Q extends Query> {
    Set<T> getTargets();

    Set<Q> getQueries(@Nonnull T target);
}
