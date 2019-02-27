package io.github.anycollect.core.impl.router;

import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.metric.MetricFamily;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ConsumeJob implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(ConsumeJob.class);
    private final MetricConsumer consumer;
    private final List<MetricFamily> families;
    private final Clock clock;

    public ConsumeJob(@Nonnull final MetricConsumer consumer,
                      @Nonnull final List<MetricFamily> families,
                      @Nonnull final Clock clock) {
        this.consumer = consumer;
        this.families = families;
        this.clock = clock;
    }

    @Override
    public void run() {
        LOG.debug("consuming {} metric families", families.size());
        long start = clock.monotonicTime();
        consumer.consume(families);
        LOG.debug("consume job completed, time: {}ms",
                TimeUnit.NANOSECONDS.toMillis(clock.monotonicTime() - start));
    }
}
