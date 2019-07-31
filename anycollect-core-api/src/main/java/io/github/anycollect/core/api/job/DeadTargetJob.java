package io.github.anycollect.core.api.job;

import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.metric.Sample;

import java.util.List;

public final class DeadTargetJob implements Job {
    @Override
    public List<Sample> execute() throws ConnectionException {
        throw new ConnectionException("target is dead");
    }
}
