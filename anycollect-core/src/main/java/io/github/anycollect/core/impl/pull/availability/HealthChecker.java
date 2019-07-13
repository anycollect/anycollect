package io.github.anycollect.core.impl.pull.availability;

import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;

import javax.annotation.Nonnull;

public interface HealthChecker<T extends Target, Q extends Query<T>> {
    @SuppressWarnings("rawtypes")
    HealthChecker NOOP = new HealthChecker() {
        @Override
        public void add(@Nonnull final CheckingTarget checkingTarget) {

        }

        @Override
        public void remove(@Nonnull final CheckingTarget checkingTarget) {

        }
    };

    @SuppressWarnings("unchecked")
    static <T extends Target, Q extends Query<T>> HealthChecker<T, Q> noop() {
        return (HealthChecker<T, Q>) NOOP;
    }

    void add(@Nonnull CheckingTarget<T> checkingTarget);

    void remove(@Nonnull CheckingTarget<T> checkingTarget);
}
