package io.github.anycollect.core.impl.pull;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.api.internal.DesiredStateProvider;
import io.github.anycollect.core.api.internal.PullManager;
import io.github.anycollect.core.api.internal.QueryMatcherResolver;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.query.QueryProvider;
import io.github.anycollect.core.api.target.ServiceDiscovery;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.core.impl.pull.separate.SchedulerFactoryImpl;
import io.github.anycollect.core.impl.pull.separate.SeparatePullScheduler;
import io.github.anycollect.core.impl.scheduler.Scheduler;
import io.github.anycollect.core.impl.pull.separate.SchedulerFactory;
import io.github.anycollect.core.impl.scheduler.SchedulerImpl;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Extension(name = "PullEngine", point = PullManager.class)
public final class PullManagerImpl implements PullManager {
    private static final Logger LOG = LoggerFactory.getLogger(PullManagerImpl.class);
    private final PullScheduler puller;
    private final Scheduler updater;
    private final int updatePeriodInSeconds;
    private final int defaultPullPeriodInSeconds;

    @ExtCreator
    public PullManagerImpl(@Nonnull @ExtConfig final PullManagerConfig config) {
        LOG.debug("create pull manager with config {}", config);
        this.updatePeriodInSeconds = config.getUpdatePeriodInSeconds();
        this.defaultPullPeriodInSeconds = config.getDefaultPullPeriodInSeconds();
        this.updater = new SchedulerImpl(new ScheduledThreadPoolExecutor(1));
        SchedulerFactory schedulerFactory = new SchedulerFactoryImpl(
                config.getConcurrencyRule(), config.getDefaultPoolSize());
        this.puller = new SeparatePullScheduler(schedulerFactory, Clock.getDefault());
    }

    public PullManagerImpl(@Nonnull final PullScheduler puller,
                           @Nonnull final Scheduler updater,
                           final int updatePeriodInSeconds,
                           final int defaultPullPeriodInSeconds) {
        this.puller = puller;
        this.updater = updater;
        this.updatePeriodInSeconds = updatePeriodInSeconds;
        this.defaultPullPeriodInSeconds = defaultPullPeriodInSeconds;
    }

    @Override
    public <T extends Target<Q>, Q extends Query> void start(@Nonnull final ServiceDiscovery<T> discovery,
                                                             @Nonnull final QueryProvider<Q> provider,
                                                             @Nonnull final QueryMatcherResolver<T, Q> resolver,
                                                             @Nonnull final Dispatcher dispatcher) {
        start(new StdDesiredStateProvider<>(
                discovery,
                provider,
                resolver,
                defaultPullPeriodInSeconds), dispatcher);
    }

    @Override
    public <T extends Target<Q>, Q extends Query> void start(@Nonnull final DesiredStateProvider<T, Q> stateProvider,
                                                             @Nonnull final Dispatcher dispatcher) {
        ResultCallback<T, Q> callback = new CallbackToDispatcherAdapter<>(dispatcher);
        DesiredStateManager<T, Q> desiredStateManager = new DesiredStateManagerImpl<>(puller, callback);
        DesiredStateUpdateJob<T, Q> job = new DesiredStateUpdateJob<>(stateProvider, desiredStateManager);
        updater.scheduleAtFixedRate(job, updatePeriodInSeconds, TimeUnit.SECONDS);
    }
}
