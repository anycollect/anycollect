package io.github.anycollect.core.impl.router;

import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public final class RouteDispatcherFanout implements RouteDispatcher {
    private final List<? extends MetricConsumer> consumers;

    public RouteDispatcherFanout(@Nonnull final List<? extends MetricConsumer> consumers) {
        this.consumers = consumers;
    }

    @Override
    public void dispatch(@Nonnull final Metric metric) {
        for (MetricConsumer consumer : consumers) {
            consumer.consume(Collections.singletonList(metric));
        }
    }

    @Override
    public void dispatch(@Nonnull final List<Metric> metrics) {
        for (MetricConsumer consumer : consumers) {
            consumer.consume(metrics);
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
