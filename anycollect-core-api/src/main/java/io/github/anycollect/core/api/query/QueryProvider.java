package io.github.anycollect.core.api.query;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

public interface QueryProvider<Q extends Query> {
    static <Q extends Query> CompositeQueryProvider<Q> composite(final List<? extends QueryProvider<? extends Q>> providers) {
        return new CompositeQueryProvider<>(providers);
    }

    @Nonnull
    Set<Q> provide();
}
