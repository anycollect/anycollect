package io.github.anycollect.readers.jmx.query;

import io.github.anycollect.metric.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.anycollect.assertj.SamplesAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JvmThreadsTest {
    private MBeanServer server;
    private JvmThreads jvmThreads;

    @BeforeEach
    void setUp() {
        server = MBeanServerFactory.createMBeanServer();
        jvmThreads = new JvmThreads();
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
        List<Sample> samples = new MockJavaApp(server).bind(jvmThreads).execute()
                .stream().map(sample -> sample.getMetric().sample(sample.getValue(), 0))
                .collect(Collectors.toList());
        assertThat(samples)
                .contains(Metric.builder().key("jvm/threads/live").tag("type", "daemon").empty().gauge().sample(1, 0))
                .contains(Metric.builder().key("jvm/threads/live").tag("type", "nondaemon").empty().gauge().sample(2, 0))
                .contains(Metric.builder().key("jvm/threads/started").empty().empty().counter().sample(5, 0))
                .contains(Metric.builder().key("jvm/threads/states").tag("state", "RUNNABLE").empty().gauge().sample(1, 0));
    }
}