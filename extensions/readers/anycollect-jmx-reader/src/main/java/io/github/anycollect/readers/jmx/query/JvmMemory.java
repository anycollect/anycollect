package io.github.anycollect.readers.jmx.query;

import com.fasterxml.jackson.annotation.JacksonInject;
import io.github.anycollect.core.api.job.Job;
import io.github.anycollect.core.api.job.TaggingJob;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.ImmutableTags;
import io.github.anycollect.metric.Measurement;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.readers.jmx.server.JavaApp;
import io.github.anycollect.readers.jmx.query.operations.QueryAttributes;
import io.github.anycollect.readers.jmx.query.operations.QueryObjectNames;
import io.github.anycollect.readers.jmx.query.operations.QueryOperation;

import javax.annotation.Nonnull;
import javax.management.Attribute;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class JvmMemory extends JmxQuery {
    private static final ObjectName MEMORY_POOL_OBJECT_PATTERN;
    private static final String NAME_ATTR_NAME = "Name";
    private static final String USAGE_ATTR_NAME = "Usage";
    private static final String TYPE_ATTR_NAME = "Type";
    private static final String HEAP_TYPE = "HEAP";
    private static final String USED = "used";
    private static final String[] ATTRIBUTES = new String[]{NAME_ATTR_NAME, USAGE_ATTR_NAME, TYPE_ATTR_NAME};

    static {
        try {
            MEMORY_POOL_OBJECT_PATTERN = new ObjectName("java.lang:type=MemoryPool,name=*");
        } catch (MalformedObjectNameException e) {
            throw new RuntimeException("could not create memory pool object name", e);
        }
    }

    private final Clock clock;

    public JvmMemory(@JacksonInject @Nonnull final Clock clock) {
        super("jvm.memory");
        this.clock = clock;
    }

    @Nonnull
    @Override
    public Job bind(@Nonnull final JavaApp app) {
        return new TaggingJob(
                Tags.concat(app.getTags(), getTags()),
                Tags.concat(app.getMeta(), getTags()),
                new JvmMemoryJob(app));
    }

    private final class JvmMemoryJob implements Job {
        private final JavaApp app;
        private final QueryOperation<Set<ObjectName>> queryNames;

        JvmMemoryJob(final JavaApp app) {
            this.app = app;
            this.queryNames = new QueryObjectNames(MEMORY_POOL_OBJECT_PATTERN);
        }

        @Override
        public List<Metric> execute() throws QueryException, ConnectionException {
            List<Metric> metrics = new ArrayList<>();
            for (ObjectName objectName : app.operate(queryNames)) {
                List<Attribute> attributes = app.operate(new QueryAttributes(objectName, ATTRIBUTES));
                long timestamp = clock.wallTime();
                String name = (String) attributes.get(0).getValue();
                CompositeData usage = (CompositeData) attributes.get(1).getValue();
                long used = (Long) usage.get(USED);
                String type = HEAP_TYPE.equals(attributes.get(2).getValue()) ? "heap" : "nonheap";
                ImmutableTags tags = Tags.builder()
                        .tag("pool", name.replace(' ', '_'))
                        .tag("type", type)
                        .build();
                Metric metric = Metric.of("jvm.memory.used", tags, Tags.empty(),
                        Measurement.gauge(used, "bytes"), timestamp);
                metrics.add(metric);
            }
            return metrics;
        }
    }
}
