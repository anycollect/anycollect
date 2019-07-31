package io.github.anycollect.core.api.dispatcher;

import io.github.anycollect.metric.Sample;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

@FunctionalInterface
public interface Dispatcher {
    Dispatcher NOOP = new Dispatcher() {
        @Override
        public void dispatch(@Nonnull final Sample sample) {
        }

        @Override
        public void dispatch(@Nonnull final List<Sample> samples) {
        }
    };

    static Dispatcher noop() {
        return NOOP;
    }

    static Accumulator accumulator() {
        return new Accumulator();
    }

    default void dispatch(@Nonnull Sample sample) {
        dispatch(Collections.singletonList(sample));
    }

    void dispatch(@Nonnull List<Sample> samples);
}
