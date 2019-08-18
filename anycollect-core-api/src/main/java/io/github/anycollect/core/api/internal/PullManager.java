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
            @Nonnull String token,
            @Nonnull ServiceDiscovery<? extends T> discovery,
            @Nonnull QueryProvider<? extends Q> provider,
            @Nonnull QueryMatcherResolver resolver,
            @Nonnull Dispatcher dispatcher
    );

    <Q extends SelfQuery> Cancellation start(@Nonnull String token,
                                             @Nonnull Q selfQuery,
                                             @Nonnull Dispatcher dispatcher);

    <Q extends SelfQuery> Cancellation start(@Nonnull String token, @Nonnull Q selfQuery,
                                             @Nonnull Dispatcher dispatcher,
                                             int periodInSeconds);

    <T extends Target, Q extends Query<T>> Cancellation start(@Nonnull String token,
                                                              @Nonnull DesiredStateProvider<T, Q> stateProvider,
                                                              @Nonnull Dispatcher dispatcher);
}
