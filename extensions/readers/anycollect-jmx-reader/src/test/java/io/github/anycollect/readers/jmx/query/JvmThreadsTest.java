package io.github.anycollect.readers.jmx.query;

import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Stat;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.metric.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.List;

import static io.github.anycollect.assertj.AnyCollectAssertions.assertThatFamilies;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JvmThreadsTest {
    private MBeanServer server;
    private JvmThreads jvmThreads;

    @BeforeEach
    void setUp() {
        server = MBeanServerFactory.createMBeanServer();
        jvmThreads = new JvmThreads(Clock.getDefault());
    }

    @Test
    void threadsMetrics() throws Exception {
        ThreadMXBean thread = mock(ThreadMXBean.class);
        long[] ids = {0};
        when(thread.getAllThreadIds()).thenReturn(ids);
        when(thread.getThreadCount()).thenReturn(3);
        when(thread.getDaemonThreadCount()).thenReturn(1);
        when(thread.getTotalStartedThreadCount()).thenReturn(5L);
        Thread test = new Thread();
        test.setName("test");
        ThreadInfo info = mock(ThreadInfo.class);
        when(info.getThreadState()).thenReturn(Thread.State.RUNNABLE);
        ThreadInfo[] infos = new ThreadInfo[]{info};
        when(thread.getThreadInfo(ids)).thenReturn(infos);
        server.registerMBean(thread, new ObjectName("java.lang:type=Threading"));
        List<Metric> families = jvmThreads.executeOn(server, Tags.empty());
        assertThatFamilies(families)
                .contains("jvm.threads.live", Tags.of("type", "daemon"))
                .hasMeasurement(Stat.value(), Type.GAUGE, "threads", 1);
        assertThatFamilies(families)
                .contains("jvm.threads.live", Tags.of("type", "nondaemon"))
                .hasMeasurement(Stat.value(), Type.GAUGE, "threads", 2);
        assertThatFamilies(families)
                .contains("jvm.threads.started")
                .hasMeasurement(Stat.value(), Type.COUNTER, "threads", 5);
        assertThatFamilies(families)
                .contains("jvm.threads.states", Tags.of("state", "RUNNABLE"))
                .hasMeasurement(Stat.value(), Type.GAUGE, "threads", 1);
    }
}