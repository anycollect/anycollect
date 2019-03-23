package io.github.anycollect.readers.jmx.query;

import io.github.anycollect.core.api.job.Job;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.readers.jmx.server.JavaApp;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class NoopQuery extends JmxQuery {
    public NoopQuery(@Nonnull String id) {
        super(id);
    }

    @Nonnull
    @Override
    public Job bind(@Nonnull JavaApp app) {
        return new Job() {
            @Override
            public List<Metric> execute() throws QueryException, ConnectionException {
                return Collections.emptyList();
            }
        };
    }
}
