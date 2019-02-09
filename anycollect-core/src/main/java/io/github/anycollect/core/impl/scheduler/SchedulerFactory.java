package io.github.anycollect.core.impl.scheduler;

import javax.annotation.Nonnull;

public interface SchedulerFactory {
    @Nonnull
    Scheduler create();
}
