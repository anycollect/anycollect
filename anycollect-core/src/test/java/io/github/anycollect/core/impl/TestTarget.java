package io.github.anycollect.core.impl;

import io.github.anycollect.core.api.job.Job;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.Metric;

import javax.annotation.Nonnull;
import java.util.List;

public interface TestTarget extends Target<TestQuery> {
    @Nonnull
    @Override
    default Job bind(@Nonnull TestQuery query) {
        return new TestJob(this, query);
    }

    List<Metric> execute(TestQuery query) throws QueryException, ConnectionException;
}
