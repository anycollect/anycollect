package io.github.anycollect.core.api.job;

import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.Sample;

import java.util.List;

public interface Job {
    List<Sample> execute() throws InterruptedException, QueryException, ConnectionException;
}
