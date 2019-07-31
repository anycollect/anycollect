package io.github.anycollect.core.impl.router;

import io.github.anycollect.metric.Sample;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public final class RouteDispatcherFanout implements RouteDispatcher {
    private final List<? extends MetricConsumer> consumers;

    public RouteDispatcherFanout(@Nonnull final List<? extends MetricConsumer> consumers) {
        this.consumers = consumers;
    }

    @Override
    public void dispatch(@Nonnull final Sample sample) {
        for (MetricConsumer consumer : consumers) {
            consumer.consume(Collections.singletonList(sample));
        }
    }

    @Override
    public void dispatch(@Nonnull final List<Sample> samples) {
        for (MetricConsumer consumer : consumers) {
            consumer.consume(samples);
        }
    }

    @Override
    public void stop() {
        for (MetricConsumer consumer : consumers) {
            consumer.stop();
        }
    }

    @Override
    public String toString() {
        return consumers.toString();
    }
}
