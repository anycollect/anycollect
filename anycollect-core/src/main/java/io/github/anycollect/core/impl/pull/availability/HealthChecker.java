package io.github.anycollect.core.impl.pull.availability;

import io.github.anycollect.core.api.internal.State;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;

import javax.annotation.Nonnull;

public interface HealthChecker<T extends Target<Q>, Q extends Query> {
    @SuppressWarnings("rawtypes")
    HealthChecker NOOP = new HealthChecker() {
        @Override
        public void stop() {
        }

        @Override
        public void update(@Nonnull final State state) {
        }
    };

    @SuppressWarnings("unchecked")
    static <T extends Target<Q>, Q extends Query> HealthChecker<T, Q> noop() {
        return (HealthChecker<T, Q>) NOOP;
    }

    void stop();

    void update(@Nonnull State<T, Q> state);
}
