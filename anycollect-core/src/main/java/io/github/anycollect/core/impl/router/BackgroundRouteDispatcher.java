package io.github.anycollect.core.impl.router;

import io.github.anycollect.metric.MetricFamily;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

@ThreadSafe
public final class BackgroundRouteDispatcher implements RouteDispatcher {
    private final ExecutorService executor;
    private final MetricConsumer consumer;
    private volatile boolean stopped = false;

    public BackgroundRouteDispatcher(@Nonnull final ExecutorService executor,
                                     @Nonnull final MetricConsumer consumer) {
        this.executor = executor;
        this.consumer = consumer;
    }

    @Override
    public void dispatch(@Nonnull final MetricFamily family) {
        dispatch(Collections.singletonList(family));
    }

    @Override
    public void dispatch(@Nonnull final List<MetricFamily> families) {
        if (!stopped) {
            executor.submit(() -> consumer.consume(families));
        }
    }

    @Override
    public void stop() {
        stopped = true;
        executor.shutdownNow();
    }

    @Override
    public String toString() {
        return consumer.getAddress();
    }
}
