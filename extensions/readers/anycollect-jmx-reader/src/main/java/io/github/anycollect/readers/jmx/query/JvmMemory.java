package io.github.anycollect.readers.jmx.query;

import com.fasterxml.jackson.annotation.JacksonInject;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.metric.ImmutableTags;
import io.github.anycollect.metric.Measurement;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.readers.jmx.server.JavaApp;

import javax.annotation.Nonnull;
import javax.management.*;
import javax.management.openmbean.CompositeData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JvmMemory extends JmxQuery {
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
    public List<Metric> executeOn(@Nonnull final MBeanServerConnection connection,
                                  @Nonnull final JavaApp app) throws ConnectionException {
        List<Metric> families = new ArrayList<>();
        for (ObjectName objectName : queryNames(connection, MEMORY_POOL_OBJECT_PATTERN)) {
            AttributeList attributes;
            try {
                attributes = connection.getAttributes(objectName, ATTRIBUTES);
            } catch (InstanceNotFoundException | ReflectionException | IOException e) {
                throw new ConnectionException("could not get attributes", e);
            }
            long timestamp = clock.wallTime();
            String name = (String) ((Attribute) attributes.get(0)).getValue();
            CompositeData usage = (CompositeData) ((Attribute) attributes.get(1)).getValue();
            long used = (Long) usage.get(USED);
            String type = HEAP_TYPE.equals(((Attribute) attributes.get(2)).getValue()) ? "heap" : "nonheap";
            ImmutableTags tags = Tags.builder()
                    .concat(app.getTags())
                    .tag("pool", name.replace(' ', '_'))
                    .tag("type", type)
                    .build();
            Metric family = Metric.of("jvm.memory.used", tags, app.getMeta(),
                    Measurement.gauge(used, "bytes"), timestamp);
            families.add(family);
        }
        return families;
    }
}
