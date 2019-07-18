package io.github.anycollect.core.api.dispatcher;

import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

@FunctionalInterface
public interface Dispatcher {
    Dispatcher NOOP = new Dispatcher() {
        @Override
        public void dispatch(@Nonnull final Metric metric) {
        }

        @Override
        public void dispatch(@Nonnull final List<Metric> metrics) {
        }
    };

    static Dispatcher noop() {
        return NOOP;
    }

    static Accumulator accumulator() {
        return new Accumulator();
    }

    default void dispatch(@Nonnull Metric metric) {
        dispatch(Collections.singletonList(metric));
    }

    void dispatch(@Nonnull List<Metric> metrics);
}
