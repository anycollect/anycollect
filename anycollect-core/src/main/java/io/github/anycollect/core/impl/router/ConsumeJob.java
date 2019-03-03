package io.github.anycollect.core.impl.router;

import io.github.anycollect.metric.MetricFamily;

import javax.annotation.Nonnull;
import java.util.List;

public final class ConsumeJob implements Runnable {
    private final MetricConsumer consumer;
    private final List<MetricFamily> families;

    public ConsumeJob(@Nonnull final MetricConsumer consumer,
                      @Nonnull final List<MetricFamily> families) {
        this.consumer = consumer;
        this.families = families;
    }

    @Override
    public void run() {
        consumer.consume(families);
    }
}
