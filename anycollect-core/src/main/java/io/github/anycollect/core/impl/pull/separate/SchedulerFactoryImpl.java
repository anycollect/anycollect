package io.github.anycollect.core.impl.pull.separate;

import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.core.impl.scheduler.Scheduler;
import io.github.anycollect.core.impl.scheduler.SchedulerImpl;

import javax.annotation.Nonnull;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public final class SchedulerFactoryImpl implements SchedulerFactory {
    private final ConcurrencyRule rule;
    private final int defaultPoolSize;

    public SchedulerFactoryImpl(@Nonnull final ConcurrencyRule rule, final int defaultPoolSize) {
        this.rule = rule;
        this.defaultPoolSize = defaultPoolSize;
    }

    @Nonnull
    @Override
    public Scheduler create(final Target<?> target) {
        int poolSize = rule.getPoolSize(target, defaultPoolSize);
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(poolSize);
        return new SchedulerImpl(executorService);
    }
}
