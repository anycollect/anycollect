package io.github.anycollect.readers.jmx.query;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.ImmutableTags;
import io.github.anycollect.metric.Measurement;
import io.github.anycollect.metric.MetricFamily;
import io.github.anycollect.metric.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.management.*;
import javax.management.openmbean.CompositeData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JvmThreads extends JmxQuery {
    private static final Logger LOG = LoggerFactory.getLogger(JvmThreads.class);
    private static final ObjectName THREADING_OBJECT_NAME;
    private static final String THREAD_COUNT_ATTR_NAME = "ThreadCount";
    private static final String DAEMON_THREAD_COUNT_ATTR_NAME = "DaemonThreadCount";
    private static final String ALL_THREAD_IDS_ATTR_NAME = "AllThreadIds";
    private static final String TOTAL_STARTED_THREAD_COUNT_ATTR_NAME = "TotalStartedThreadCount";
    private static final String GET_THREAD_INFO_OP_NAME = "getThreadInfo";
    private static final String THREAD_STATE_PROP = "threadState";
    private static final String[] ATTRIBUTE_NAMES = new String[]{
            THREAD_COUNT_ATTR_NAME,
            DAEMON_THREAD_COUNT_ATTR_NAME,
            ALL_THREAD_IDS_ATTR_NAME,
            TOTAL_STARTED_THREAD_COUNT_ATTR_NAME
    };

    static {
        try {
            THREADING_OBJECT_NAME = new ObjectName("java.lang:type=Threading");
        } catch (MalformedObjectNameException e) {
            throw new RuntimeException("could not create threading object name", e);
        }
    }

    private static final String LIVE_THREADS_KEY = "jvm.threads.live";
    private static final String THREADS_STARTED_KEY = "jvm.threads.started";
    private static final String THREADS_BY_STATE_KEY = "jvm.threads.states";
    private static final String THREADS_UNIT = "threads";

    private final Clock clock;

    public JvmThreads(@JacksonInject final Clock clock) {
        super("jvm.threads");
        this.clock = clock;
    }

    @Nonnull
    @Override
    public List<MetricFamily> executeOn(@Nonnull final MBeanServerConnection connection,
                                        @Nonnull final Tags targetTags) throws QueryException, ConnectionException {
        AttributeList attributes;
        try {
            attributes = connection.getAttributes(THREADING_OBJECT_NAME, ATTRIBUTE_NAMES);
        } catch (InstanceNotFoundException | ReflectionException | IOException e) {
            throw new ConnectionException("could not query attributes", e);
        }
        int threadCount = (int) ((Attribute) attributes.get(0)).getValue();
        int daemonThreadCount = (int) ((Attribute) attributes.get(1)).getValue();
        long[] allThreadIds = (long[]) ((Attribute) attributes.get(2)).getValue();
        long totalStartedThreadCount = (long) ((Attribute) attributes.get(3)).getValue();
        Object[] params = new Object[]{allThreadIds};
        CompositeData[] threadInfos;
        try {
            threadInfos = (CompositeData[]) connection.invoke(
                    THREADING_OBJECT_NAME,
                    GET_THREAD_INFO_OP_NAME,
                    params,
                    new String[]{long[].class.getName()});
        } catch (InstanceNotFoundException | MBeanException | ReflectionException e) {
            LOG.debug("could not invoke method", e);
            throw new QueryException("could not invoke method", e);
        } catch (IOException e) {
            throw new ConnectionException("could not invoke method", e);
        }

        long timestamp = clock.wallTime();

        List<MetricFamily> families = new ArrayList<>();
        families.add(MetricFamily.of(
                THREADS_STARTED_KEY,
                targetTags,
                Tags.empty(),
                Measurement.counter(totalStartedThreadCount, THREADS_UNIT),
                timestamp
        ));
        families.add(MetricFamily.of(
                LIVE_THREADS_KEY,
                Tags.concat(targetTags, Tags.of("type", "daemon")),
                Tags.empty(),
                Measurement.gauge(daemonThreadCount, THREADS_UNIT),
                timestamp));
        families.add(MetricFamily.of(
                LIVE_THREADS_KEY,
                Tags.concat(targetTags, Tags.of("type", "nondaemon")),
                Tags.empty(),
                Measurement.gauge(threadCount - daemonThreadCount, THREADS_UNIT),
                timestamp));
        Multiset<String> numberOfThreadsByState = HashMultiset.create();
        for (CompositeData threadInfo : threadInfos) {
            String state = (String) threadInfo.get(THREAD_STATE_PROP);
            numberOfThreadsByState.add(state);
        }
        for (String state : numberOfThreadsByState.elementSet()) {
            ImmutableTags tags = Tags.builder()
                    .concat(targetTags)
                    .tag("state", state)
                    .build();
            families.add(MetricFamily.of(
                    THREADS_BY_STATE_KEY,
                    tags,
                    Tags.empty(),
                    Measurement.gauge(numberOfThreadsByState.count(state), THREADS_UNIT),
                    timestamp));
        }
        return families;
    }
}
