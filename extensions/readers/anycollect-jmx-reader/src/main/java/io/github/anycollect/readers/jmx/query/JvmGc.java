package io.github.anycollect.readers.jmx.query;

import com.sun.management.GarbageCollectionNotificationInfo;
import com.sun.management.GcInfo;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.api.job.Job;
import io.github.anycollect.core.api.job.TaggingJob;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.readers.jmx.query.operations.QueryObjectNames;
import io.github.anycollect.readers.jmx.query.operations.SubscribeOperation;
import io.github.anycollect.readers.jmx.query.operations.Subscription;
import io.github.anycollect.readers.jmx.server.JavaApp;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import java.lang.management.MemoryUsage;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class JvmGc extends JmxQuery {
    private static final Logger LOG = LoggerFactory.getLogger(JvmGc.class);
    private static final ObjectName GARBAGE_COLLECTORS;
    private final String prefix;
    private final Clock clock;

    static {
        try {
            GARBAGE_COLLECTORS = new ObjectName("java.lang:type=GarbageCollector,name=*");
        } catch (MalformedObjectNameException e) {
            throw new RuntimeException("could not create garbage collector object name", e);
        }
    }

    public JvmGc(@Nonnull final String prefix,
                 @Nonnull final Tags tags,
                 @Nonnull final Tags meta) {
        super("jvm.gc", tags, meta);
        this.prefix = prefix;
        this.clock = Clock.getDefault();
    }

    @Nonnull
    @Override
    public Job bind(@Nonnull final JavaApp app) {
        return new TaggingJob(prefix, app, this, new JvmGcJob(app));
    }

    private final class JvmGcJob implements Job, NotificationListener {
        private final JavaApp app;
        @GuardedBy("lock")
        private List<GarbageCollectionNotificationInfo> accumulated = new ArrayList<>();
        private final Object lock = new Object();
        private Subscription subscription;
        private final Set<GcId> ids = Collections.newSetFromMap(new ConcurrentHashMap<>());

        private JvmGcJob(final JavaApp app) {
            this.app = app;
        }

        @Override
        public List<Metric> execute() throws QueryException, ConnectionException {
            // renew subscription if needed
            if (subscription == null || !subscription.isValid()) {
                Set<ObjectName> garbageCollectors = app.operate(new QueryObjectNames(GARBAGE_COLLECTORS));
                LOG.debug("subscribe to gc info updates");
                SubscribeOperation subscribe = new SubscribeOperation(garbageCollectors, this);
                this.subscription = app.operate(subscribe);
            }
            // aggregate accumulated info and purge accumulator
            return aggregate();
        }

        @Override
        public void handleNotification(final Notification notification, final Object handback) {
            if (notification.getType().equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION)) {
                GarbageCollectionNotificationInfo info
                        = GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());
                synchronized (lock) {
                    this.accumulated.add(info);
                }
            }
        }

        private List<Metric> aggregate() {
            long timestamp = clock.wallTime();
            Map<GcId, GcData> accumulator = new HashMap<>();
            List<GarbageCollectionNotificationInfo> accumulated;
            synchronized (lock) {
                accumulated = new ArrayList<>(this.accumulated);
                this.accumulated = new ArrayList<>();
            }
            for (GarbageCollectionNotificationInfo info : accumulated) {
                String gcName = info.getGcName();
                String gcCause = info.getGcCause();
                GcInfo gcInfo = info.getGcInfo();
                long duration = gcInfo.getDuration();
                Map<String, MemoryUsage> memoryUsageBeforeGc = gcInfo.getMemoryUsageBeforeGc();
                Map<String, MemoryUsage> memoryUsageAfterGc = gcInfo.getMemoryUsageAfterGc();
                long totalUsedBefore = 0;
                long totalUsedAfter = 0;
                for (String poolName : memoryUsageBeforeGc.keySet()) {
                    MemoryUsage before = memoryUsageBeforeGc.get(poolName);
                    MemoryUsage after = memoryUsageAfterGc.get(poolName);
                    long usedBefore = before.getUsed();
                    long usedAfter = after.getUsed();
                    totalUsedBefore += usedBefore;
                    totalUsedAfter += usedAfter;
                }
                GcId id = new GcId(gcName, gcCause);
                ids.add(id);
                GcData gcData = accumulator.computeIfAbsent(id, gcId -> new GcData());
                long totalFreed = totalUsedBefore - totalUsedAfter;
                gcData.freed += totalFreed;
                gcData.duration += duration;
            }
            List<Metric> metrics = new ArrayList<>();
            for (GcId id : ids) {
                GcData data = accumulator.get(id);
                String gcName = id.gcName;
                long duration = data != null ? data.duration : 0;
                long freed = data != null ? data.freed : 0;
                metrics.add(Metric.builder()
                        .key(id.concurrent ? "jvm.gc.concurrent.phase.duration" : "jvm.gc.pause")
                        .at(timestamp)
                        .tag("gcName", gcName)
                        .gauge("ms", duration)
                        .build());
                metrics.add(Metric.builder()
                        .key("jvm.gc.memory.freed")
                        .at(timestamp)
                        .tag("gcName", gcName)
                        .gauge("bytes", freed)
                        .build());
            }
            return metrics;
        }
    }

    @EqualsAndHashCode
    private static final class GcId {
        private final String gcName;
        private final boolean concurrent;

        GcId(final String gcName, final String gcCause) {
            this.gcName = gcName;
            this.concurrent = isConcurrentPhase(gcCause);
        }

        boolean isConcurrentPhase(final String gcCause) {
            return "No GC".equals(gcCause);
        }
    }

    private static final class GcData {
        private long duration = 0L;
        private long freed;
    }
}
