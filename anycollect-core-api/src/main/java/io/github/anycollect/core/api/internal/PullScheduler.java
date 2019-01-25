package io.github.anycollect.core.api.internal;

import io.github.anycollect.core.api.common.Lifecycle;
import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.ServiceDiscovery;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.core.api.query.QueryProvider;

import javax.annotation.Nonnull;

public interface PullScheduler extends Lifecycle {
    <T extends Target, Q extends Query> void start(
            @Nonnull PullJobFactory<T, Q> factory,
            @Nonnull ServiceDiscovery<T> discovery,
            @Nonnull QueryProvider<Q> provider,
            @Nonnull Dispatcher dispatcher
    );
}
