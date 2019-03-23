package io.github.anycollect.core.api.query;

import io.github.anycollect.core.api.job.Job;
import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class SelfQuery extends AbstractQuery implements Job {
    public SelfQuery(@Nonnull final String id) {
        super(id);
    }

    @Override
    public abstract List<Metric> execute();
}
