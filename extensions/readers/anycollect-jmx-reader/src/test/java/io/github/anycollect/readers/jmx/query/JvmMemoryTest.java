package io.github.anycollect.readers.jmx.query;

import io.github.anycollect.metric.Sample;
import io.github.anycollect.metric.Tags;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.util.List;

import static io.github.anycollect.assertj.AnyCollectAssertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JvmMemoryTest {
    private MBeanServer server;
    private JvmMemory jvmMemory;

    @BeforeEach
    void setUp() {
        server = MBeanServerFactory.createMBeanServer();
        jvmMemory = new JvmMemory();
    }

    @Test
    void usedMemory() throws Exception {
        MemoryPoolMXBean memoryPool = mock(MemoryPoolMXBean.class);
        when(memoryPool.getName()).thenReturn("Test");
        MemoryUsage heap = new MemoryUsage(1, 2, 3, 4);
        when(memoryPool.getType()).thenReturn(MemoryType.HEAP);
        when(memoryPool.getUsage()).thenReturn(heap);
        server.registerMBean(memoryPool, new ObjectName("java.lang:type=MemoryPool,name=Test"));
        List<Sample> samples = new MockJavaApp(server, Tags.of("instance", "test")).bind(jvmMemory).execute();
        assertThat(samples).hasSize(1);
        assertThat(samples.get(0))
                .hasKey("jvm/memory/used")
                .hasGauge("bytes", 2.0)
                .hasTags("instance", "test",
                        "pool", "Test",
                        "type", "heap");
    }
}