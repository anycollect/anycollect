package io.github.anycollect.core.api.query;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public final class CompositeQueryProvider<Q extends Query> implements QueryProvider<Q> {
    private final List<? extends QueryProvider<? extends Q>> providers;

    public CompositeQueryProvider(final List<? extends QueryProvider<? extends Q>> providers) {
        this.providers = new ArrayList<>(providers);
    }

    @Nonnull
    @Override
    public Set<Q> provide() {
        return providers.stream().flatMap(provider -> provider.provide().stream())
                .collect(toSet());
    }
}
