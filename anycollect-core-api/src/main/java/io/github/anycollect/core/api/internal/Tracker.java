package io.github.anycollect.core.api.internal;

import io.github.anycollect.metric.Point;

import javax.annotation.Nonnull;

public interface Tracker {
    void dropWrite(@Nonnull Point point);

    void acceptWrite(@Nonnull Point point);
}
