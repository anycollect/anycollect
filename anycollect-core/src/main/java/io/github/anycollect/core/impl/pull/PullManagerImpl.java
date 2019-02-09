package io.github.anycollect.core.impl.pull;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.internal.DesiredStateProvider;
import io.github.anycollect.core.api.internal.PullManager;
import io.github.anycollect.core.api.internal.QueryMatcherResolver;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.query.QueryProvider;
import io.github.anycollect.core.api.target.ServiceDiscovery;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.core.impl.scheduler.Scheduler;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

public final class PullManagerImpl implements PullManager {
    private final PullScheduler puller;
    private final Scheduler updater;
    private final int updateIntervalInSeconds;
    private final int defaultPullPeriodInSeconds;

    public PullManagerImpl(@Nonnull final PullScheduler puller,
                           @Nonnull final Scheduler updater,
                           final int updateIntervalInSeconds,
                           final int defaultPullPeriodInSeconds) {
        this.puller = puller;
        this.updater = updater;
        this.updateIntervalInSeconds = updateIntervalInSeconds;
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
        updater.scheduleAtFixedRate(job, updateIntervalInSeconds, TimeUnit.SECONDS);
    }

}
