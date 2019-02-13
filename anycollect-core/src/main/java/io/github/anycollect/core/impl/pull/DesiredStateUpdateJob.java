package io.github.anycollect.core.impl.pull;

import io.github.anycollect.core.api.internal.DesiredStateProvider;
import io.github.anycollect.core.api.internal.State;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public final class DesiredStateUpdateJob<T extends Target<Q>, Q extends Query> implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(DesiredStateUpdateJob.class);
    private final DesiredStateProvider<T, Q> stateProvider;
    private final DesiredStateManager<T, Q> scheduler;

    public DesiredStateUpdateJob(@Nonnull final DesiredStateProvider<T, Q> stateProvider,
                                 @Nonnull final DesiredStateManager<T, Q> scheduler) {
        this.stateProvider = stateProvider;
        this.scheduler = scheduler;
    }

    @Override
    public void run() {
        LOG.debug("getting new desired state");
        State<T, Q> state = stateProvider.current();
        LOG.debug("updating state");
        scheduler.update(state);
        LOG.debug("state has been successfully updated");
    }
}