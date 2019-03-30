package io.github.anycollect.readers.jmx.query;

import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.api.job.Job;
import io.github.anycollect.core.api.job.TaggingJob;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.readers.jmx.query.operations.QueryAttributes;
import io.github.anycollect.readers.jmx.query.operations.QueryObjectNames;
import io.github.anycollect.readers.jmx.query.operations.QueryOperation;
import io.github.anycollect.readers.jmx.server.JavaApp;

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

    private final String prefix;
    private final Clock clock;

    public JvmMemory() {
        this("", Tags.empty(), Tags.empty());
    }

    public JvmMemory(@Nonnull final String prefix,
                     @Nonnull final Tags tags,
                     @Nonnull final Tags meta) {
        super("jvm.memory", tags, meta);
        this.prefix = prefix;
        this.clock = Clock.getDefault();
    }

    @Nonnull
    @Override
    public Job bind(@Nonnull final JavaApp app) {
        return new TaggingJob(prefix, app, this, new JvmMemoryJob(app));
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
                metrics.add(Metric.builder()
                        .key("jvm.memory.used")
                        .tag("pool", name)
                        .tag("type", type)
                        .at(timestamp)
                        .gauge("bytes", used)
                        .build());
            }
            return metrics;
        }
    }
}
