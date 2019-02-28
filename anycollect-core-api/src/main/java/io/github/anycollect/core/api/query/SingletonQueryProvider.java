package io.github.anycollect.core.api.query;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;

public class SingletonQueryProvider<Q extends Query> implements QueryProvider<Q> {
    private final Set<Q> singleton;

    public SingletonQueryProvider(@Nonnull final Q query) {
        this.singleton = Collections.singleton(query);
    }

    @Nonnull
    @Override
    public Set<Q> provide() {
        return singleton;
    }
}
