package io.github.anycollect.readers.jmx.query;

import io.github.anycollect.core.api.query.AbstractQuery;
import io.github.anycollect.metric.MetricFamily;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import javax.management.MBeanServerConnection;
import java.util.List;

@ToString
@ThreadSafe
@EqualsAndHashCode(callSuper = true)
public abstract class JmxQuery extends AbstractQuery {
    public JmxQuery(@Nonnull final String id) {
        super(id);
    }

    @Nonnull
    public abstract List<MetricFamily> executeOn(@Nonnull MBeanServerConnection connection)
            throws QueryException, ConnectionException;
}
