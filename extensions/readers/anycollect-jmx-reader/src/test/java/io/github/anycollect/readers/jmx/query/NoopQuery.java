package io.github.anycollect.readers.jmx.query;

import io.github.anycollect.metric.Metric;
import io.github.anycollect.readers.jmx.ConnectionException;
import io.github.anycollect.readers.jmx.QueryException;

import javax.annotation.Nonnull;
import javax.management.MBeanServerConnection;
import java.util.Collections;
import java.util.List;

public class NoopQuery extends Query {
    public NoopQuery(@Nonnull String group, @Nonnull String label) {
        super(group, label, null);
    }

    @Nonnull
    @Override
    public List<Metric> executeOn(@Nonnull MBeanServerConnection connection) throws QueryException, ConnectionException {
        return Collections.emptyList();
    }
}
