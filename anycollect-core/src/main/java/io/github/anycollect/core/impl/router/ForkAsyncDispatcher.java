package io.github.anycollect.core.impl.router;

import io.github.anycollect.metric.MetricFamily;

import javax.annotation.Nonnull;
import java.util.List;

public class ForkAsyncDispatcher implements AsyncDispatcher {
    private final List<? extends AsyncDispatcher> dispatchers;

    public ForkAsyncDispatcher(@Nonnull final List<? extends AsyncDispatcher> dispatchers) {
        this.dispatchers = dispatchers;
    }

    @Override
    public void dispatch(@Nonnull final MetricFamily family) {
        for (AsyncDispatcher dispatcher : dispatchers) {
            dispatcher.dispatch(family);
        }
    }

    @Override
    public void dispatch(@Nonnull final List<MetricFamily> families) {
        for (AsyncDispatcher dispatcher : dispatchers) {
            dispatcher.dispatch(families);
        }
    }

    @Override
    public void stop() {
        for (AsyncDispatcher dispatcher : dispatchers) {
            dispatcher.stop();
        }
    }

    @Override
    public String toString() {
        return dispatchers.toString();
    }
}
