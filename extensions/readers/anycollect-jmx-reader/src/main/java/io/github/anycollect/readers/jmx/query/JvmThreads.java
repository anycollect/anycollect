package io.github.anycollect.readers.jmx.query;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.api.job.Job;
import io.github.anycollect.core.api.job.TaggingJob;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.readers.jmx.query.operations.InvokeOperation;
import io.github.anycollect.readers.jmx.query.operations.QueryAttributes;
import io.github.anycollect.readers.jmx.query.operations.QueryOperation;
import io.github.anycollect.readers.jmx.server.JavaApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.management.Attribute;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import java.util.ArrayList;
import java.util.List;

public final class JvmThreads extends JmxQuery {
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

    public JvmThreads() {
        super("jvm.threads");
        this.clock = Clock.getDefault();
    }

    @Nonnull
    @Override
    public Job bind(@Nonnull final JavaApp app) {
        return new TaggingJob(
                Tags.concat(app.getTags(), getTags()),
                Tags.concat(app.getMeta(), getMeta()),
                new JvmThreadsJob(app));
    }

    private final class JvmThreadsJob implements Job {
        private final JavaApp app;
        private final QueryOperation<List<Attribute>> queryAttributes;

        JvmThreadsJob(final JavaApp app) {
            this.app = app;
            this.queryAttributes = new QueryAttributes(THREADING_OBJECT_NAME, ATTRIBUTE_NAMES);
        }

        @Override
        public List<Metric> execute() throws QueryException, ConnectionException {
            List<Attribute> attributes = app.operate(queryAttributes);
            int threadCount = (int) attributes.get(0).getValue();
            int daemonThreadCount = (int) attributes.get(1).getValue();
            long[] allThreadIds = (long[]) attributes.get(2).getValue();
            long totalStartedThreadCount = (long) attributes.get(3).getValue();
            Object[] params = new Object[]{allThreadIds};
            CompositeData[] threadInfos;
            InvokeOperation invoke = new InvokeOperation(THREADING_OBJECT_NAME,
                    GET_THREAD_INFO_OP_NAME,
                    params,
                    new String[]{long[].class.getName()});
            threadInfos = (CompositeData[]) app.operate(invoke);
            long timestamp = clock.wallTime();
            List<Metric> metrics = new ArrayList<>();
            metrics.add(Metric.builder()
                    .key(THREADS_STARTED_KEY)
                    .at(timestamp)
                    .counter(THREADS_UNIT, totalStartedThreadCount)
                    .build());
            metrics.add(Metric.builder()
                    .key(LIVE_THREADS_KEY)
                    .tag("type", "daemon")
                    .at(timestamp)
                    .gauge(THREADS_UNIT, daemonThreadCount)
                    .build());
            metrics.add(Metric.builder()
                    .key(LIVE_THREADS_KEY)
                    .tag("type", "nondaemon")
                    .at(timestamp)
                    .gauge(THREADS_UNIT, threadCount - daemonThreadCount)
                    .build());
            Multiset<String> numberOfThreadsByState = HashMultiset.create();
            for (CompositeData threadInfo : threadInfos) {
                String state = (String) threadInfo.get(THREAD_STATE_PROP);
                numberOfThreadsByState.add(state);
            }
            for (String state : numberOfThreadsByState.elementSet()) {
                metrics.add(Metric.builder()
                        .key(THREADS_BY_STATE_KEY)
                        .tag("state", state)
                        .at(timestamp)
                        .gauge(THREADS_UNIT, numberOfThreadsByState.count(state))
                        .build());
            }
            return metrics;
        }
    }
}
