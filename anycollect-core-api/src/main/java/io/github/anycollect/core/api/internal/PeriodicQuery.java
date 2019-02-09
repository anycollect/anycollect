package io.github.anycollect.core.api.internal;

public interface PeriodicQuery<Q> {
    Q getQuery();

    int getPeriodInSeconds();
}
