package io.github.anycollect.core.api.internal;

import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;

public interface State<T extends Target<Q>, Q extends Query> {
    @SuppressWarnings("rawtypes")
    State EMPTY = new State() {
        @Override
        public Set getTargets() {
            return Collections.EMPTY_SET;
        }

        @Override
        public Set getQueries(@Nonnull final Target target) {
            return Collections.EMPTY_SET;
        }
    };

    @SuppressWarnings("unchecked")
    static <T extends Target<Q>, Q extends Query> State<T, Q> empty() {
        return (State<T, Q>) EMPTY;
    }

    Set<T> getTargets();

    Set<PeriodicQuery<Q>> getQueries(@Nonnull T target);

}
