package io.github.anycollect.core.api.target;

import io.github.anycollect.core.api.common.Lifecycle;

import javax.annotation.Nonnull;

public interface ServiceDiscovery<T extends Target> extends Lifecycle {
    void start(@Nonnull Targets<T> targets);
}
