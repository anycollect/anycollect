package io.github.anycollect.readers.jmx.module;

import io.github.anycollect.metric.MetricId;
import io.github.anycollect.metric.Type;
import io.github.anycollect.readers.jmx.query.AnyCollectQuery;
import io.github.anycollect.readers.jmx.query.Query;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.*;

import static io.github.anycollect.readers.jmx.monitoring.MonitoringConstants.*;

public final class JmxReaderQueryModule implements QueryModule {
    private final Set<Query> queries;

    public JmxReaderQueryModule() {
        Set<Query> tmp = new HashSet<>();
        tmp.add(connectionPoolMetric(CONNECTION_POOL_IDLE, Type.GAUGE));
        tmp.add(connectionPoolMetric(CONNECTION_POOL_ACTIVE, Type.GAUGE));
        tmp.add(connectionPoolMetric(CONNECTION_POOL_INVALIDATED, Type.COUNTER));
        this.queries = tmp;
    }

    private AnyCollectQuery connectionPoolMetric(final String what, final Type type) {
        Hashtable<String, String> tags = new Hashtable<>();
        tags.put(MetricId.METRIC_KEY_TAG, what);
        tags.put(MetricId.METRIC_TYPE_TAG, type.getTagValue());
        tags.put(MetricId.UNIT_TAG, CONNECTIONS_UNIT);
        tags.put(APPLICATION_TAG, "*");
        tags.put(SERVER_TAG, "*");
        try {
            ObjectName objectName = new ObjectName("anycollect", tags);
            return new AnyCollectQuery("JmxReader", what, null, objectName);
        } catch (MalformedObjectNameException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public Set<Query> getQueries() {
        return Collections.unmodifiableSet(queries);
    }
}
