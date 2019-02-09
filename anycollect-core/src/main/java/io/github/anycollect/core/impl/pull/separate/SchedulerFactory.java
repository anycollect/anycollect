package io.github.anycollect.core.impl.pull.separate;

import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.core.impl.scheduler.Scheduler;

import javax.annotation.Nonnull;

public interface SchedulerFactory {
    @Nonnull
    Scheduler create(Target<?> target);
}
