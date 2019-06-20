package io.github.anycollect.core.api.internal;

import javax.annotation.Nonnull;

public interface QueryMatcherResolver {
    static QueryMatcherResolver consistent(@Nonnull QueryMatcher matcher) {
        return new ConsistentQueryMatcherResolver(matcher);
    }

    static QueryMatcherResolver alwaysAll(int period) {
        return consistent(QueryMatcher.all(period));
    }

    @Nonnull
    QueryMatcher current();
}
