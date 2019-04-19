package io.github.anycollect.readers.process;

import io.github.anycollect.core.api.job.DeadTargetJob;
import io.github.anycollect.core.api.job.Job;
import io.github.anycollect.metric.Tags;

import javax.annotation.Nonnull;

/**
 * The system process that have to be live but it is not.
 */
public final class EphemeralProcess extends Process {
    public EphemeralProcess(@Nonnull final String id, @Nonnull final Tags tags, @Nonnull final Tags meta) {
        super(id, tags, meta);
    }

    @Nonnull
    @Override
    public Job bind(@Nonnull final ProcessQuery query) {
        return new DeadTargetJob();
    }
}
