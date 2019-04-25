package io.github.anycollect.core.impl.pull;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.core.impl.pull.availability.CheckingTarget;
import io.github.anycollect.core.impl.scheduler.Cancellation;

import javax.annotation.Nonnull;

public interface PullScheduler {
    /**
     * Creates and executes periodic pull job with the given period.
     *
     * @param target          - the target to pull metrics from
     * @param query           - the query
     * @param dispatcher      - dispatcher to send results to
     * @param periodInSeconds - the period in seconds between successive executions
     * @param <T>             - the type target
     * @param <Q>             - the type query
     * @return the cancellation to cancel scheduled job
     */
    @Nonnull
    <T extends Target<Q>, Q extends Query> Cancellation schedulePull(
            @Nonnull CheckingTarget<T> target, @Nonnull Q query, @Nonnull Dispatcher dispatcher, int periodInSeconds);

    /**
     * Signals that the target is no longer needed to be monitored.
     * <p>
     * All jobs associated with given target must be terminated. If there is no jobs associated with target
     * nothing happens
     *
     * @param target - target that must be unmonitored
     */
    void release(@Nonnull Target<?> target);
}
