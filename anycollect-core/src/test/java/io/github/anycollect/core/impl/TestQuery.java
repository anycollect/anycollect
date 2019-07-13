package io.github.anycollect.core.impl;

import io.github.anycollect.core.api.job.Job;
import io.github.anycollect.core.api.query.AbstractQuery;

import javax.annotation.Nonnull;

public class TestQuery extends AbstractQuery<TestTarget> {
    public TestQuery(@Nonnull final String id) {
        super(id);
    }

    @Nonnull
    @Override
    public Job bind(@Nonnull TestTarget target) {
        return new TestJob(target, this);
    }
}
