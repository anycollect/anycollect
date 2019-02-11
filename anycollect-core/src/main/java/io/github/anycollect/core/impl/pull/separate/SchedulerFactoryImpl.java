package io.github.anycollect.core.impl.pull.separate;

import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.core.impl.scheduler.MonitoredScheduledThreadPoolExecutor;
import io.github.anycollect.core.impl.scheduler.Scheduler;
import io.github.anycollect.core.impl.scheduler.SchedulerImpl;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public final class SchedulerFactoryImpl implements SchedulerFactory {
    private static final Logger LOG = LoggerFactory.getLogger(SchedulerFactoryImpl.class);
    private final ConcurrencyRule rule;
    private final int defaultPoolSize;
    private final MeterRegistry registry;

    public SchedulerFactoryImpl(@Nonnull final ConcurrencyRule rule,
                                final int defaultPoolSize,
                                @Nonnull final MeterRegistry registry) {
        this.rule = rule;
        this.defaultPoolSize = defaultPoolSize;
        this.registry = registry;
    }

    // TODO ThreadFactory
    @Nonnull
    @Override
    public Scheduler create(final Target<?> target) {
        int poolSize = rule.getPoolSize(target, defaultPoolSize);
        LOG.debug("creating scheduler for target {} with pool size {}", target, poolSize);
        ScheduledThreadPoolExecutor executorService
                = new MonitoredScheduledThreadPoolExecutor(poolSize, registry, "target", target.getLabel());
        return new SchedulerImpl(executorService);
    }
}
