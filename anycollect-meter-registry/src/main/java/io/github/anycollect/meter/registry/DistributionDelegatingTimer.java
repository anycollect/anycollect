package io.github.anycollect.meter.registry;

import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.metric.MeterId;
import io.github.anycollect.metric.Sample;
import io.github.anycollect.metric.Timer;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DistributionDelegatingTimer extends AbstractMeter implements Timer {
    private final DistributionMeter distribution;
    private final TimeUnit timeUnit;
    private final Clock clock;

    public DistributionDelegatingTimer(@Nonnull final MeterId id,
                                       @Nonnull final DistributionMeter distribution,
                                       @Nonnull final TimeUnit timeUnit) {
        super(id);
        this.distribution = distribution;
        this.timeUnit = timeUnit;
        this.clock = Clock.getDefault();
    }

    @Override
    public void record(final long amount, @Nonnull final TimeUnit timeUnit) {
        long convertedAmount = this.timeUnit.convert(amount, timeUnit);
        distribution.record(convertedAmount);
    }

    @Override
    public void record(@Nonnull final Runnable runnable) {
        long start = clock.monotonicTime();
        runnable.run();
        long end = clock.monotonicTime();
        record(end - start, TimeUnit.NANOSECONDS);
    }

    @Override
    public List<Sample> measure() {
        return distribution.measure();
    }
}
