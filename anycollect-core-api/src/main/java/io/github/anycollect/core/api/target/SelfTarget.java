package io.github.anycollect.core.api.target;

import io.github.anycollect.core.api.job.Job;
import io.github.anycollect.core.api.query.SelfQuery;
import io.github.anycollect.metric.Tags;

import javax.annotation.Nonnull;

public final class SelfTarget extends AbstractTarget<SelfQuery> {
    public SelfTarget(@Nonnull final String id) {
        super(id, Tags.empty(), Tags.empty());
    }

    @Nonnull
    @Override
    public Job bind(@Nonnull final SelfQuery query) {
        return query;
    }
}
