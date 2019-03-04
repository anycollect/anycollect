package io.github.anycollect.core.api.dispatcher;

import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import java.util.List;

public interface Dispatcher {
    Dispatcher NOOP = new Dispatcher() {
        @Override
        public void dispatch(@Nonnull final Metric family) {
        }

        @Override
        public void dispatch(@Nonnull final List<Metric> families) {
        }
    };

    static Dispatcher noop() {
        return NOOP;
    }

    void dispatch(@Nonnull Metric family);

    void dispatch(@Nonnull List<Metric> families);
}
