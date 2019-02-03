package io.github.anycollect.core.impl;

import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.time.Duration;

public final class SubmitJob implements Runnable {
    @Getter
    private final Duration repeatInterval;
    private final Puller puller;
    private final PullJob<?, ?> job;

    public <T extends Target<Q>, Q extends Query> SubmitJob(
            @Nonnull final T target,
            @Nonnull final Q query,
            @Nonnull final Duration repeatInterval,
            @Nonnull final Puller puller,
            @Nonnull final ResultCallback<T, Q> callback,
            @Nonnull final Clock clock) {
        this.puller = puller;
        this.job = new PullJob<>(target, query, callback, clock);
        this.repeatInterval = repeatInterval;
    }

    @Override
    public void run() {
        PullJob.State state = job.getState();
        if (state == PullJob.State.INITIALIZED) {
            job.submit();
            puller.pullAsync(job);
        } else if (state == PullJob.State.COMPLETED) {
            job.renew();
            job.submit();
            puller.pullAsync(job);
        }
        // TODO else
    }
}
