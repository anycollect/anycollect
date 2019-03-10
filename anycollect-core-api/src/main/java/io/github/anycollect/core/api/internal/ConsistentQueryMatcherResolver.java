package io.github.anycollect.core.api.internal;

import javax.annotation.Nonnull;

public final class ConsistentQueryMatcherResolver implements QueryMatcherResolver {
    private final QueryMatcher matcher;

    public ConsistentQueryMatcherResolver(@Nonnull final QueryMatcher matcher) {
        this.matcher = matcher;
    }

    @Nonnull
    @Override
    public QueryMatcher current() {
        return matcher;
    }
}
