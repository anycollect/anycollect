package io.github.anycollect.core.impl;

import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.Sample;

import java.util.List;

public interface TestTarget extends Target {
    List<Sample> execute(TestQuery query) throws QueryException, ConnectionException;
}
