package io.github.anycollect.core.api.internal;

import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public interface Result {
    @Nonnull
    Query getQuery();

    @Nonnull
    Target getTarget();

    @Nonnull
    List<Metric> getMetrics();

    boolean isSuccess();

    boolean isCanceled();

    @Nonnull
    Optional<Exception> getException();
}
