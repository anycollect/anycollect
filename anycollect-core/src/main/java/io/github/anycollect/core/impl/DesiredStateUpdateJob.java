package io.github.anycollect.core.impl;

import io.github.anycollect.core.api.internal.DesiredStateProvider;
import io.github.anycollect.core.api.internal.State;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;

import javax.annotation.Nonnull;

public final class DesiredStateUpdateJob<T extends Target<Q>, Q extends Query> implements Runnable {
    private final DesiredStateProvider<T, Q> stateProvider;
    private final DesiredStateManager<T, Q> scheduler;

    public DesiredStateUpdateJob(@Nonnull final DesiredStateProvider<T, Q> stateProvider,
                                 @Nonnull final DesiredStateManager<T, Q> scheduler) {
        this.stateProvider = stateProvider;
        this.scheduler = scheduler;
    }

    @Override
    public void run() {
        State<T, Q> state = stateProvider.current();
        scheduler.update(state);
    }
}
