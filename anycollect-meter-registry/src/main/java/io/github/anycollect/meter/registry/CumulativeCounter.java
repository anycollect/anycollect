package io.github.anycollect.meter.registry;

import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.metric.*;
import lombok.Builder;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.DoubleAdder;

public class CumulativeCounter extends AbstractMeter implements Counter, Meter {
    private final Clock clock;
    private final DoubleAdder adder = new DoubleAdder();
    private final Metric id;

    @Builder
    public CumulativeCounter(@Nonnull final MeterId id,
                             @Nonnull final String prefix,
                             @Nonnull final Tags tags,
                             @Nonnull final Tags meta,
                             @Nonnull final Clock clock) {
        super(id);
        this.clock = clock;
        this.id = Metric.builder()
                .key(id.getKey().withPrefix(prefix))
                .tags(tags.concat(id.getTags()))
                .meta(meta.concat(id.getMetaTags()))
                .counter(id.getUnit());
    }

    @Override
    public void increment(final double amount) {
        adder.add(amount);
    }

    @Nonnull
    @Override
    public List<Sample> measure() {
        return Collections.singletonList(id.sample(adder.longValue(), clock.wallTime()));
    }
}
