package io.github.anycollect.readers.jmx.application;

import io.github.anycollect.readers.jmx.query.Query;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class OrQueryMatcher implements QueryMatcher {
    private final List<QueryMatcher> matchers;

    public OrQueryMatcher(@Nonnull final List<QueryMatcher> matchers) {
        Objects.requireNonNull(matchers, "matchers must not be null");
        this.matchers = new ArrayList<>(matchers);
    }

    @Override
    public boolean matches(@Nonnull final Query query) {
        return matchers.stream().anyMatch(matcher -> matcher.matches(query));
    }
}
