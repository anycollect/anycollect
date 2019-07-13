package io.github.anycollect.core.impl.pull.separate;

import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.impl.pull.PullScheduler;
import io.github.anycollect.core.impl.pull.PullSchedulerFactory;
import io.github.anycollect.metric.MeterRegistry;

import javax.annotation.Nonnull;

public final class SeparatePullSchedulerFactory implements PullSchedulerFactory {
    private final SchedulerFactory schedulerFactory;
    private final MeterRegistry meterRegistry;
    private final Clock clock;

    public SeparatePullSchedulerFactory(@Nonnull final SchedulerFactory schedulerFactory,
                                        @Nonnull final MeterRegistry meterRegistry) {
        this.schedulerFactory = schedulerFactory;
        this.meterRegistry = meterRegistry;
        this.clock = Clock.getDefault();
    }

    @Override
    public PullScheduler newScheduler() {
        return new SeparatePullScheduler(schedulerFactory, meterRegistry, clock);
    }
}
