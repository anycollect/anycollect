package io.github.anycollect.core.impl.pull.separate;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.core.impl.scheduler.MonitoredScheduledThreadPoolExecutor;
import io.github.anycollect.core.impl.scheduler.Scheduler;
import io.github.anycollect.core.impl.scheduler.SchedulerImpl;
import io.github.anycollect.meter.api.MeterRegistry;
import io.github.anycollect.metric.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

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

    @Nonnull
    @Override
    public Scheduler create(@Nonnull final Target target, @Nonnull final String group) {
        int poolSize = rule.getPoolSize(target, defaultPoolSize);
        LOG.info("Creating scheduler for target {} with pool size {}", target, poolSize);
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("anycollect-pull-(" + target.getId() + ")-[%d]")
                .build();
        Tags tags = Tags.of("group", group, "target", target.getId());
        String prefix = "pull";
        ScheduledThreadPoolExecutor executorService = new MonitoredScheduledThreadPoolExecutor(poolSize, threadFactory,
                registry, prefix, tags);
        return new SchedulerImpl(executorService, registry, prefix, tags);
    }
}
