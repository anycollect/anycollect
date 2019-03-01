package io.github.anycollect.meter.registry;

import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.metric.*;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.DoubleAdder;

public class CumulativeCounter extends AbstractMeter implements Counter, Measurable {
    private final Clock clock;
    private final DoubleAdder adder = new DoubleAdder();

    public CumulativeCounter(@Nonnull final MeterId id,
                             @Nonnull final Clock clock) {
        super(id);
        this.clock = clock;
    }

    @Override
    public void increment(final double amount) {
        adder.add(amount);
    }

    @Nonnull
    @Override
    public List<MetricFamily> measure() {
        Measurement counter = Measurement.counter(adder.doubleValue(), getId().getUnit());
        return Collections.singletonList(MetricFamily.of(getId(), counter, clock.wallTime()));
    }
}
