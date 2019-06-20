package io.github.anycollect.core.api.dispatcher;

import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class Accumulator implements Dispatcher {
    private final Queue<Metric> metrics;

    public Accumulator() {
        metrics = new ConcurrentLinkedQueue<>();
    }

    public List<Metric> purge() {
        return new ArrayList<>(metrics);
    }

    @Override
    public void dispatch(@Nonnull final Metric metric) {
        this.metrics.add(metric);
    }

    @Override
    public void dispatch(@Nonnull final List<Metric> metrics) {
        this.metrics.addAll(metrics);
    }
}
