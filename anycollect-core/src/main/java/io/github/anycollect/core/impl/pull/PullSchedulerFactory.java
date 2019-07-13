package io.github.anycollect.core.impl.pull;

import javax.annotation.Nonnull;

public interface PullSchedulerFactory {
    PullScheduler newScheduler(@Nonnull String name);
}
