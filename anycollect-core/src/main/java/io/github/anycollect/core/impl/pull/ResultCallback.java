package io.github.anycollect.core.impl.pull;

import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;

import javax.annotation.Nonnull;

public interface ResultCallback<T extends Target<Q>, Q extends Query> {
    @SuppressWarnings("rawtypes")
    ResultCallback NOOP = result -> {
    };

    @SuppressWarnings("unchecked")
    static <T extends Target<Q>, Q extends Query> ResultCallback<T, Q> noop() {
        return (ResultCallback<T, Q>) NOOP;
    }

    void call(@Nonnull Result<T, Q> result);
}
