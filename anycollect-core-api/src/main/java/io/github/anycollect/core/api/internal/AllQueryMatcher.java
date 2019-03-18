package io.github.anycollect.core.api.internal;

import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class AllQueryMatcher implements QueryMatcher {
    private final int period;

    public AllQueryMatcher(final int period) {
        this.period = period;
    }

    @Override
    public int getPeriodInSeconds(@Nonnull final Target target, @Nonnull final Query query, final int defaultPeriod) {
        Objects.requireNonNull(target, "target must not be null");
        Objects.requireNonNull(query, "query must not be null");
        if (defaultPeriod <= 0) {
            throw new IllegalArgumentException("default period must be greater than zero, given: " + defaultPeriod);
        }
        if (period <= 0) {
            return defaultPeriod;
        } else {
            return period;
        }
    }
}
