package io.github.anycollect.core.api.internal;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public final class ImmutablePeriodicQuery<Q> implements PeriodicQuery<Q> {
    private final Q query;
    private final int period;

    public ImmutablePeriodicQuery(final Q query, final int period) {
        this.query = query;
        this.period = period;
    }

    @Override
    public Q getQuery() {
        return query;
    }

    @Override
    public int getPeriodInSeconds() {
        return period;
    }
}
