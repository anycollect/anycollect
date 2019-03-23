package io.github.anycollect.core.impl;

import io.github.anycollect.core.api.job.Job;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.Metric;

import java.util.List;

public class TestJob implements Job {
    private final TestTarget target;
    private final TestQuery query;

    public TestJob(TestTarget target, TestQuery query) {
        this.target = target;
        this.query = query;
    }

    @Override
    public List<Metric> execute() throws QueryException, ConnectionException {
        return target.execute(query);
    }
}
