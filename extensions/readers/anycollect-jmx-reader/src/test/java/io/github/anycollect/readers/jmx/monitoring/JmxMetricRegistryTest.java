package io.github.anycollect.readers.jmx.monitoring;

import io.github.anycollect.metric.MetricId;
import io.github.anycollect.metric.Type;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JmxMetricRegistryTest {
    private MBeanServer mBeanServer;
    private JmxMetricRegistry jmxMetricRegistry;

    @BeforeEach
    void setUp() {
        mBeanServer = mock(MBeanServer.class);
        jmxMetricRegistry = new JmxMetricRegistry(mBeanServer);
    }

    @Test
    void mustHandleRegistrationExceptions() throws Exception {
        when(mBeanServer.registerMBean(any(), any())).thenThrow(
                new InstanceAlreadyExistsException(),
                new MBeanRegistrationException(null),
                new NotCompliantMBeanException()
        );
        MetricId id = MetricId.builder().key("test").unit("tests").type(Type.GAUGE).build();
        Assertions.assertDoesNotThrow(() -> jmxMetricRegistry.register(id, () -> 0));
        Assertions.assertDoesNotThrow(() -> jmxMetricRegistry.register(id, () -> 0));
        Assertions.assertDoesNotThrow(() -> jmxMetricRegistry.register(id, () -> 0));
    }
}