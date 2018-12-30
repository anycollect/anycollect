package io.github.anycollect.readers.jmx.query;

import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.MetricId;
import io.github.anycollect.readers.jmx.ConnectionException;
import io.github.anycollect.readers.jmx.QueryException;
import io.github.anycollect.readers.jmx.monitoring.JmxUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class AnyCollectQuery extends Query {
    private final ObjectName objectPattern;

    public AnyCollectQuery(@Nonnull final String group,
                           @Nonnull final String label,
                           @Nullable final Duration interval,
                           @Nonnull final ObjectName objectPattern) {
        super(group, label, interval);
        this.objectPattern = objectPattern;
    }

    @Nonnull
    @Override
    public List<Metric> executeOn(@Nonnull final MBeanServerConnection connection)
            throws QueryException, ConnectionException {
        List<Metric> metrics = new ArrayList<>();
        long timestamp = System.currentTimeMillis();
        try {
            Set<ObjectName> objectNames = connection.queryNames(objectPattern, null);
            for (ObjectName objectName : objectNames) {
                MetricId id = JmxUtils.convert(objectName);
                Double value = (Double) connection.getAttribute(objectName, "Value");
                metrics.add(new Metric(id, value, timestamp));
            }
        } catch (IOException e) {
            throw new ConnectionException("exception during execution", e);
        } catch (Exception e) {
            throw new QueryException(e);
        }
        return metrics;
    }
}
