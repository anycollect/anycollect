package io.github.anycollect.core.api.internal;

import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.core.api.common.Plugin;
import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.ServiceDiscovery;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.core.api.query.QueryProvider;

import javax.annotation.Nonnull;

public interface PullManager extends Plugin, Lifecycle {
    <T extends Target<Q>, Q extends Query> void start(
            @Nonnull ServiceDiscovery<T> discovery,
            @Nonnull QueryProvider<Q> provider,
            @Nonnull QueryMatcherResolver<T, Q> matcher,
            @Nonnull Dispatcher dispatcher
    );

    <T extends Target<Q>, Q extends Query> void start(@Nonnull DesiredStateProvider<T, Q> stateProvider);
}
