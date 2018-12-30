package io.github.anycollect.readers.jmx.query;

import io.github.anycollect.metric.Metric;

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
    public List<Metric> executeOn(@Nonnull MBeanServerConnection connection) {
        return Collections.emptyList();
    }
}
