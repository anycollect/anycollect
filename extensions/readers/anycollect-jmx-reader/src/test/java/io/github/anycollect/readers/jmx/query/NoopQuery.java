package io.github.anycollect.readers.jmx.query;

import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.MetricFamily;
import io.github.anycollect.metric.Tags;

import javax.annotation.Nonnull;
import javax.management.MBeanServerConnection;
import java.util.Collections;
import java.util.List;

public class NoopQuery extends JmxQuery {
    public NoopQuery(@Nonnull String id) {
        super(id);
    }

    @Nonnull
    @Override
    public List<MetricFamily> executeOn(@Nonnull MBeanServerConnection connection, @Nonnull Tags targetTags) throws QueryException, ConnectionException {
        return Collections.emptyList();
    }
}
