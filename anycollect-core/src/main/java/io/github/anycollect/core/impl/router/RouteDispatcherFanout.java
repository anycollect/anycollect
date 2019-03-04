package io.github.anycollect.core.impl.router;

import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import java.util.List;

public final class RouteDispatcherFanout implements RouteDispatcher {
    private final List<? extends RouteDispatcher> dispatchers;

    public RouteDispatcherFanout(@Nonnull final List<? extends RouteDispatcher> dispatchers) {
        this.dispatchers = dispatchers;
    }

    @Override
    public void dispatch(@Nonnull final Metric family) {
        for (RouteDispatcher dispatcher : dispatchers) {
            dispatcher.dispatch(family);
        }
    }

    @Override
    public void dispatch(@Nonnull final List<Metric> families) {
        for (RouteDispatcher dispatcher : dispatchers) {
            dispatcher.dispatch(families);
        }
    }

    @Override
    public void stop() {
        for (RouteDispatcher dispatcher : dispatchers) {
            dispatcher.stop();
        }
    }

    @Override
    public String toString() {
        return dispatchers.toString();
    }
}
