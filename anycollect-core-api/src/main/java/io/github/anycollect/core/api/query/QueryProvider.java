package io.github.anycollect.core.api.query;

import javax.annotation.Nonnull;

public interface QueryProvider<Q extends Query> {
    void start(@Nonnull Queries<Q> queries);
}
