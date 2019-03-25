package io.github.anycollect.meter.registry;

import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.metric.*;
import io.github.anycollect.metric.prepared.PreparedMetric;
import lombok.Builder;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.DoubleAdder;

public class CumulativeCounter extends AbstractMeter implements Counter, Meter {
    private final Clock clock;
    private final DoubleAdder adder = new DoubleAdder();
    private final PreparedMetric preparedCounter;

    @Builder
    public CumulativeCounter(@Nonnull final MeterId id,
                             @Nonnull final String prefix,
                             @Nonnull final Tags tags,
                             @Nonnull final Tags meta,
                             @Nonnull final Clock clock) {
        super(id);
        this.clock = clock;
        this.preparedCounter = Metric.prepare()
                .key(prefix, id.getKey())
                .concatTags(tags)
                .concatTags(id.getTags())
                .concatMeta(meta)
                .concatMeta(id.getMetaTags())
                .counter(id.getUnit())
                .build();
    }

    @Override
    public void increment(final double amount) {
        adder.add(amount);
    }

    @Nonnull
    @Override
    public List<Metric> measure() {
        return Collections.singletonList(preparedCounter.compile(clock.wallTime(), adder.doubleValue()));
    }
}
