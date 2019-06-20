package io.github.anycollect.core.api.job;

import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.Metric;

import java.util.List;

public interface Job {
    List<Metric> execute() throws InterruptedException, QueryException, ConnectionException;
}
