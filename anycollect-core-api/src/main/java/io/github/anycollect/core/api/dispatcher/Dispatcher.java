package io.github.anycollect.core.api.dispatcher;

import io.github.anycollect.metric.MetricFamily;

import javax.annotation.Nonnull;
import java.util.List;

public interface Dispatcher {
    Dispatcher NOOP = new Dispatcher() {
        @Override
        public void dispatch(@Nonnull final MetricFamily family) {
        }

        @Override
        public void dispatch(@Nonnull final List<MetricFamily> families) {
        }
    };

    static Dispatcher noop() {
        return NOOP;
    }

    void dispatch(@Nonnull MetricFamily family);

    void dispatch(@Nonnull List<MetricFamily> families);
}
