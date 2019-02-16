package io.github.anycollect.core.api.internal;

import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;

import javax.annotation.Nonnull;
import java.util.Objects;

public interface QueryMatcher {
    QueryMatcher ALL = (target, query, defaultPeriod) -> {
        Objects.requireNonNull(target, "target must not be null");
        Objects.requireNonNull(query, "query must not be null");
        if (defaultPeriod <= 0) {
            throw new IllegalArgumentException("default period must be greater than zero, given: " + defaultPeriod);
        }
        return defaultPeriod;
    };

    static QueryMatcher all() {
        return ALL;
    }

    /**
     * Returns period in which query should be invoked on target.
     * If given query must not be executed on the target, -1 must be returned
     *
     * @param target        - target when query is going to be executed, not null
     * @param query         - query, not null
     * @param defaultPeriod - default period in seconds, greater than zero
     * @return period if given query must be executed on given target and -1 otherwise
     */
    int getPeriodInSeconds(@Nonnull Target target, @Nonnull Query query, int defaultPeriod);
}
