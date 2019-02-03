package io.github.anycollect.core.api.internal;

import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;

import javax.annotation.Nonnull;

public interface QueryMatcher<T extends Target<Q>, Q extends Query> {
    @SuppressWarnings("rawtypes")
    QueryMatcher ALL = (target, query) -> true;

    @SuppressWarnings("unchecked")
    static <T extends Target<Q>, Q extends Query> QueryMatcher<T, Q> all() {
        return (QueryMatcher<T, Q>) ALL;
    }

    /**
     * Returns true if given query must be executed on the target
     *
     * @param target - target when query is going to be executed, not null
     * @param query  - query, not null
     * @return true if given query must be executed on given target and else otherwise
     */
    boolean matches(@Nonnull T target, @Nonnull Q query);
}
