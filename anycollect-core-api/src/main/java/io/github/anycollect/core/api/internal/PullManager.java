package io.github.anycollect.core.api.internal;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.query.QueryProvider;
import io.github.anycollect.core.api.query.SelfQuery;
import io.github.anycollect.core.api.target.ServiceDiscovery;
import io.github.anycollect.core.api.target.Target;

import javax.annotation.Nonnull;

public interface PullManager {
    <T extends Target, Q extends Query<T>> Cancellation start(
            @Nonnull ServiceDiscovery<? extends T> discovery,
            @Nonnull QueryProvider<? extends Q> provider,
            @Nonnull QueryMatcherResolver resolver,
            @Nonnull Dispatcher dispatcher,
            @Nonnull HealthCheckConfig healthCheckConfig
    );

    <Q extends SelfQuery> Cancellation start(@Nonnull Q selfQuery, @Nonnull Dispatcher dispatcher);

    <Q extends SelfQuery> Cancellation start(@Nonnull Q selfQuery, @Nonnull Dispatcher dispatcher, int periodInSeconds);

    default <T extends Target, Q extends Query<T>> Cancellation start(@Nonnull DesiredStateProvider<T, Q> stateProvider,
                                                                      @Nonnull Dispatcher dispatcher) {
        return start(stateProvider, dispatcher, HealthCheckConfig.DISABLED);
    }

    <T extends Target, Q extends Query<T>> Cancellation start(@Nonnull DesiredStateProvider<T, Q> stateProvider,
                                                              @Nonnull Dispatcher dispatcher,
                                                              @Nonnull HealthCheckConfig healthCheckConfig);

}
