package io.github.anycollect.core.api.internal;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.query.QueryProvider;
import io.github.anycollect.core.api.query.SelfQuery;
import io.github.anycollect.core.api.target.ServiceDiscovery;
import io.github.anycollect.core.api.target.Target;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface PullManager {
    default <T extends Target<Q>, Q extends Query> void start(
            @Nonnull ServiceDiscovery<? extends T> discovery,
            @Nonnull QueryProvider<? extends Q> provider,
            @Nonnull QueryMatcherResolver resolver,
            @Nonnull Dispatcher dispatcher
    ) {
        start(discovery, provider, resolver, dispatcher, null);
    }

    <T extends Target<Q>, Q extends Query> void start(
            @Nonnull ServiceDiscovery<? extends T> discovery,
            @Nonnull QueryProvider<? extends Q> provider,
            @Nonnull QueryMatcherResolver resolver,
            @Nonnull Dispatcher dispatcher,
            @Nullable Q healthCheck
    );

    <Q extends SelfQuery> void start(@Nonnull Q selfQuery, @Nonnull Dispatcher dispatcher);

    <Q extends SelfQuery> void start(@Nonnull Q selfQuery, @Nonnull Dispatcher dispatcher, int periodInSeconds);

    <T extends Target<Q>, Q extends Query> void start(@Nonnull DesiredStateProvider<T, Q> stateProvider,
                                                      @Nonnull Dispatcher dispatcher,
                                                      @Nonnull Q healthCheck);

}
