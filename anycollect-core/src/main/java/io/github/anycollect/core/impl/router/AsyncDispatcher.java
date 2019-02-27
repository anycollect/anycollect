package io.github.anycollect.core.impl.router;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.metric.MetricFamily;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

@ThreadSafe
public class AsyncDispatcher implements Dispatcher {
    private final ExecutorService executor;
    private final MetricConsumer consumer;
    private final Clock clock;
    private volatile boolean stopped = false;

    public AsyncDispatcher(@Nonnull final ExecutorService executor,
                           @Nonnull final MetricConsumer consumer,
                           @Nonnull final Clock clock) {
        this.executor = executor;
        this.consumer = consumer;
        this.clock = clock;
    }

    @Override
    public void dispatch(@Nonnull final MetricFamily family) {
        dispatch(Collections.singletonList(family));
    }

    @Override
    public void dispatch(@Nonnull final List<MetricFamily> families) {
        ConsumeJob job = new ConsumeJob(consumer, families, clock);
        if (!stopped) {
            executor.submit(job);
        }
    }

    public void stop() {
        stopped = true;
    }
}
