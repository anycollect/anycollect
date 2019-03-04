package io.github.anycollect.core.impl.pull;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.api.internal.DesiredStateProvider;
import io.github.anycollect.core.api.internal.PullManager;
import io.github.anycollect.core.api.internal.QueryMatcherResolver;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.query.QueryProvider;
import io.github.anycollect.core.api.target.ServiceDiscovery;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.core.impl.pull.availability.HealthChecker;
import io.github.anycollect.core.impl.pull.availability.HealthCheckerImpl;
import io.github.anycollect.core.impl.pull.availability.HealthChecksConfig;
import io.github.anycollect.core.impl.pull.separate.SchedulerFactory;
import io.github.anycollect.core.impl.pull.separate.SchedulerFactoryImpl;
import io.github.anycollect.core.impl.pull.separate.SeparatePullScheduler;
import io.github.anycollect.core.impl.scheduler.Scheduler;
import io.github.anycollect.core.impl.scheduler.SchedulerImpl;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.ExtDependency;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.metric.MeterRegistry;
import io.github.anycollect.metric.noop.NoopMeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Extension(name = "PullEngine", point = PullManager.class)
public final class PullManagerImpl implements PullManager {
    private static final Logger LOG = LoggerFactory.getLogger(PullManagerImpl.class);
    private final PullScheduler puller;
    private final Scheduler updater;
    private final int updatePeriodInSeconds;
    private final int defaultPullPeriodInSeconds;
    private final HealthChecksConfig healthChecks;
    private final Scheduler healthCheckScheduler;
    private final Clock clock;
    private final MeterRegistry registry;

    @ExtCreator
    public PullManagerImpl(
            @ExtDependency(qualifier = "registry", optional = true) @Nullable final MeterRegistry optRegistry,
            @ExtConfig @Nonnull final PullManagerConfig config) {
        LOG.debug("create pull manager with config {}", config);
        this.updatePeriodInSeconds = config.getUpdatePeriodInSeconds();
        this.defaultPullPeriodInSeconds = config.getDefaultPullPeriodInSeconds();
        ThreadFactory updaterThreads = new ThreadFactoryBuilder()
                .setNameFormat("anycollect-state-updater-[%d]")
                .build();
        this.updater = new SchedulerImpl(new ScheduledThreadPoolExecutor(1, updaterThreads));
        this.registry = optRegistry != null ? optRegistry : new NoopMeterRegistry();
        SchedulerFactory schedulerFactory = new SchedulerFactoryImpl(
                config.getConcurrencyRule(), config.getDefaultPoolSize(), registry);
        this.puller = new SeparatePullScheduler(schedulerFactory, this.registry, Clock.getDefault());
        this.healthChecks = config.getHealthChecks();
        ThreadFactory healthCheckThreads = new ThreadFactoryBuilder()
                .setNameFormat("anycollect-health-check-[%d]")
                .build();
        this.healthCheckScheduler = new SchedulerImpl(new ScheduledThreadPoolExecutor(1, healthCheckThreads));
        this.clock = config.getClock();
    }

    public PullManagerImpl(@Nonnull final PullScheduler puller,
                           @Nonnull final Scheduler updater,
                           @Nonnull final Scheduler healthCheckScheduler,
                           final int updatePeriodInSeconds,
                           final int defaultPullPeriodInSeconds) {
        this.puller = puller;
        this.updater = updater;
        this.updatePeriodInSeconds = updatePeriodInSeconds;
        this.defaultPullPeriodInSeconds = defaultPullPeriodInSeconds;
        this.healthChecks = HealthChecksConfig.builder().build();
        this.healthCheckScheduler = healthCheckScheduler;
        this.clock = Clock.getDefault();
        this.registry = new NoopMeterRegistry();
    }

    @Override
    public <T extends Target<Q>, Q extends Query> void start(@Nonnull final ServiceDiscovery<? extends T> discovery,
                                                             @Nonnull final QueryProvider<? extends Q> provider,
                                                             @Nonnull final QueryMatcherResolver resolver,
                                                             @Nonnull final Dispatcher dispatcher,
                                                             @Nullable final Q healthCheck) {
        DesiredStateProvider<T, Q> stateProvider = new StdDesiredStateProvider<>(
                discovery,
                provider,
                resolver,
                defaultPullPeriodInSeconds);
        start(stateProvider, dispatcher, healthCheck);
    }

    @Override
    public <T extends Target<Q>, Q extends Query> void start(@Nonnull final DesiredStateProvider<T, Q> stateProvider,
                                                             @Nonnull final Dispatcher dispatcher,
                                                             @Nullable final Q healthCheck) {
        DesiredStateManager<T, Q> desiredStateManager = new DesiredStateManagerImpl<>(puller, dispatcher);
        HealthChecker<T, Q> checker = HealthChecker.noop();
        if (healthCheck != null) {
            checker = new HealthCheckerImpl<>(
                    puller,
                    dispatcher,
                    clock,
                    healthCheckScheduler,
                    healthChecks,
                    healthCheck);
        }
        DesiredStateUpdateJob<T, Q> job = new DesiredStateUpdateJob<>(stateProvider, desiredStateManager, checker);
        updater.scheduleAtFixedRate(job, updatePeriodInSeconds, TimeUnit.SECONDS);
    }
}
