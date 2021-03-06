package io.github.anycollect.core.impl.pull;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.internal.*;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.query.QueryProvider;
import io.github.anycollect.core.api.query.SelfQuery;
import io.github.anycollect.core.api.target.SelfDiscovery;
import io.github.anycollect.core.api.target.SelfTarget;
import io.github.anycollect.core.api.target.ServiceDiscovery;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.core.impl.pull.availability.HealthChecker;
import io.github.anycollect.core.impl.pull.availability.HealthCheckerImpl;
import io.github.anycollect.core.impl.pull.separate.SchedulerFactory;
import io.github.anycollect.core.impl.pull.separate.SchedulerFactoryImpl;
import io.github.anycollect.core.impl.pull.separate.SeparatePullSchedulerFactory;
import io.github.anycollect.core.impl.scheduler.MonitoredScheduledThreadPoolExecutor;
import io.github.anycollect.core.impl.scheduler.Scheduler;
import io.github.anycollect.core.impl.scheduler.SchedulerImpl;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.ExtDependency;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.meter.api.MeterRegistry;
import io.github.anycollect.metric.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Extension(name = PullManagerImpl.NAME, contracts = PullManager.class)
public final class PullManagerImpl implements PullManager, Lifecycle {
    public static final String NAME = "PullEngine";
    private static final Logger LOG = LoggerFactory.getLogger(PullManagerImpl.class);
    private final PullSchedulerFactory pullSchedulerFactory;
    private final Scheduler updateDesiredStateScheduler;
    private final Scheduler healthCheckScheduler;
    private final SelfDiscovery selfDiscovery;
    private final int updatePeriodInSeconds;
    private final int defaultPullPeriodInSeconds;
    private final int healthCheckPeriodInSeconds;
    private final MeterRegistry registry;

    @ExtCreator
    public PullManagerImpl(
            @ExtDependency(qualifier = "self") @Nonnull final SelfDiscovery selfDiscovery,
            @ExtDependency(qualifier = "registry", optional = true) @Nullable final MeterRegistry optRegistry,
            @ExtConfig @Nonnull final PullManagerConfig config) {
        LOG.debug("create pull manager with config {}", config);
        this.selfDiscovery = selfDiscovery;
        this.updatePeriodInSeconds = config.getUpdatePeriodInSeconds();
        this.defaultPullPeriodInSeconds = config.getDefaultPullPeriodInSeconds();
        ThreadFactory updaterThreads = new ThreadFactoryBuilder()
                .setNameFormat("anycollect-state-updater-[%d]")
                .build();
        this.registry = optRegistry != null ? optRegistry : MeterRegistry.noop();
        this.updateDesiredStateScheduler = new SchedulerImpl(new MonitoredScheduledThreadPoolExecutor(1, updaterThreads, registry, "state.updater", Tags.empty()));
        SchedulerFactory schedulerFactory = new SchedulerFactoryImpl(
                config.getConcurrencyRule(), config.getDefaultPoolSize(), registry);
        this.pullSchedulerFactory = new SeparatePullSchedulerFactory(schedulerFactory, registry);
        this.healthCheckPeriodInSeconds = config.getHealthCheckPeriodInSeconds();
        ThreadFactory healthCheckThreads = new ThreadFactoryBuilder()
                .setNameFormat("anycollect-health-check-[%d]")
                .build();
        this.healthCheckScheduler = new SchedulerImpl(new MonitoredScheduledThreadPoolExecutor(1, healthCheckThreads, registry, "health.check", Tags.empty()));
    }

    @VisibleForTesting
    PullManagerImpl(@Nonnull final PullScheduler pullScheduler,
                    @Nonnull final SelfDiscovery selfDiscovery,
                    @Nonnull final Scheduler updateDesiredStateScheduler,
                    @Nonnull final Scheduler healthCheckScheduler,
                    final int updatePeriodInSeconds,
                    final int defaultPullPeriodInSeconds,
                    final int healthCheckPeriodInSeconds) {
        this.pullSchedulerFactory = (name) -> pullScheduler;
        this.selfDiscovery = selfDiscovery;
        this.updateDesiredStateScheduler = updateDesiredStateScheduler;
        this.updatePeriodInSeconds = updatePeriodInSeconds;
        this.defaultPullPeriodInSeconds = defaultPullPeriodInSeconds;
        this.healthCheckPeriodInSeconds = healthCheckPeriodInSeconds;
        this.healthCheckScheduler = healthCheckScheduler;
        this.registry = MeterRegistry.noop();
    }

    @Override
    public <T extends Target, Q extends Query<T>> Cancellation start(
            @Nonnull final String token,
            @Nonnull final ServiceDiscovery<? extends T> discovery,
            @Nonnull final QueryProvider<? extends Q> provider,
            @Nonnull final QueryMatcherResolver resolver,
            @Nonnull final Dispatcher dispatcher) {
        DesiredStateProvider<T, Q> stateProvider = new StdDesiredStateProvider<>(
                discovery,
                provider,
                resolver,
                defaultPullPeriodInSeconds);
        return start(token, stateProvider, dispatcher);
    }

    @Override
    public <Q extends SelfQuery> Cancellation start(@Nonnull final String token,
                                                    @Nonnull final Q selfQuery,
                                                    @Nonnull final Dispatcher dispatcher) {
        return start(token, selfQuery, dispatcher, defaultPullPeriodInSeconds);
    }

    @Override
    public <Q extends SelfQuery> Cancellation start(@Nonnull final String token,
                                                    @Nonnull final Q selfQuery,
                                                    @Nonnull final Dispatcher dispatcher,
                                                    final int periodInSeconds) {
        DesiredStateProvider<SelfTarget, SelfQuery> stateProvider = new StdDesiredStateProvider<>(
                selfDiscovery,
                QueryProvider.singleton(selfQuery),
                QueryMatcherResolver.consistent(QueryMatcher.all()),
                defaultPullPeriodInSeconds);
        return start(token, stateProvider, dispatcher);
    }

    @Override
    public <T extends Target, Q extends Query<T>> Cancellation start(
            @Nonnull final String token,
            @Nonnull final DesiredStateProvider<T, Q> stateProvider,
            @Nonnull final Dispatcher dispatcher) {
        HealthChecker<T, Q> checker = new HealthCheckerImpl<>(
                dispatcher,
                healthCheckScheduler,
                TimeUnit.SECONDS.toMillis(healthCheckPeriodInSeconds),
                Tags.of("check", token),
                Tags.empty());
        PullScheduler pullScheduler = pullSchedulerFactory.newScheduler(token);
        DesiredStateManager<T, Q> desiredStateManager = new DesiredStateManagerImpl<>(pullScheduler, dispatcher, checker);
        DesiredStateUpdateJob<T, Q> job = new DesiredStateUpdateJob<>(stateProvider, desiredStateManager);
        Cancellation cancellation = updateDesiredStateScheduler.scheduleAtFixedRate(job, updatePeriodInSeconds, TimeUnit.SECONDS);
        return () -> {
            cancellation.cancel();
            LOG.info("Shutdown pull scheduler {}", token);
            pullScheduler.shutdown();
        };
    }

    @Override
    public void init() {
        LOG.info("{} has been successfully initialised", NAME);
    }

    @Override
    public void destroy() {
        LOG.info("Stopping update desired state scheduler");
        updateDesiredStateScheduler.shutdown();
        LOG.info("Stopping health check scheduler");
        healthCheckScheduler.shutdown();
        LOG.info("{} has been successfully destroyed", NAME);
    }
}
