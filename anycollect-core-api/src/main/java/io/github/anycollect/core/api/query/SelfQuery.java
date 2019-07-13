package io.github.anycollect.core.api.query;

import io.github.anycollect.core.api.job.Job;
import io.github.anycollect.core.api.target.SelfTarget;
import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class SelfQuery extends AbstractQuery<SelfTarget> implements Job {
    public SelfQuery(@Nonnull final String id) {
        super(id);
    }

    @Nonnull
    @Override
    public Job bind(@Nonnull final SelfTarget target) {
        // TODO wrap to TaggingJob?
        return this;
    }

    @Override
    public abstract List<Metric> execute();
}
