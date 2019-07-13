package io.github.anycollect.core.impl.pull;

import io.github.anycollect.core.api.internal.*;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.query.QueryProvider;
import io.github.anycollect.core.api.target.ServiceDiscovery;
import io.github.anycollect.core.api.target.Target;

import javax.annotation.Nonnull;
import java.util.Set;

public final class StdDesiredStateProvider<T extends Target, Q extends Query<T>> implements DesiredStateProvider<T, Q> {
    private final ServiceDiscovery<? extends T> discovery;
    private final QueryProvider<? extends Q> provider;
    private final QueryMatcherResolver resolver;
    private final int defaultPeriodInSeconds;

    public StdDesiredStateProvider(@Nonnull final ServiceDiscovery<? extends T> discovery,
                                   @Nonnull final QueryProvider<? extends Q> provider,
                                   @Nonnull final QueryMatcherResolver resolver,
                                   final int defaultPeriodInSeconds) {
        this.discovery = discovery;
        this.provider = provider;
        this.resolver = resolver;
        this.defaultPeriodInSeconds = defaultPeriodInSeconds;
    }

    @Nonnull
    @Override
    public State<T, Q> current() {
        Set<? extends T> targets = discovery.discover();
        Set<? extends Q> queries = provider.provide();
        QueryMatcher matcher = resolver.current();
        ImmutableState.Builder<T, Q> builder = ImmutableState.builder();
        for (T target : targets) {
            for (Q query : queries) {
                int periodInSeconds = matcher.getPeriodInSeconds(target, query, defaultPeriodInSeconds);
                if (periodInSeconds > 0) {
                    builder.put(target, query, periodInSeconds);
                }
            }
        }
        return builder.build();
    }
}
