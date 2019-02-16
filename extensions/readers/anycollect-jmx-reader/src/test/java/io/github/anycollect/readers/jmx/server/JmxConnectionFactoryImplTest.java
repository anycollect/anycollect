package io.github.anycollect.readers.jmx.server;

import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.readers.jmx.config.JavaAppConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JmxConnectionFactoryImplTest {
    private JmxConnectorFactory connectorFactory;
    private JMXConnector connector;
    private MBeanServerConnection connection;

    @BeforeEach
    void setUp() throws Exception {
        connectorFactory = mock(JmxConnectorFactory.class);
        connector = mock(JMXConnector.class);
        when(connectorFactory.connect(any(), any())).thenReturn(connector);
        connection = mock(MBeanServerConnection.class);
        when(connector.getMBeanServerConnection()).thenReturn(connection);
    }

    @Test
    void credentialsLessWithoutSslTest() throws Exception {
        JavaAppConfig config = new JavaAppConfig(
                "instance-1",
                "service:jmx:sample://",
                null,
                false
        );
        JmxConnectionFactoryImpl factory = new JmxConnectionFactoryImpl(connectorFactory, config);
        JmxConnection jmxConnection = factory.createJmxConnection();
        verify(connectorFactory, times(1)).connect(
                new JMXServiceURL("service:jmx:sample://"),
                Collections.emptyMap()
        );
        assertThat(jmxConnection.getConnection()).isSameAs(connection);
    }

    @Test
    void mustWrapIOException() throws Exception {
        JavaAppConfig config = new JavaAppConfig(
                "instance-1",
                "service:jmx:sample://",
                null,
                false
        );
        JmxConnectionFactoryImpl factory = new JmxConnectionFactoryImpl(connectorFactory, config);
        IOException cause = new IOException();
        when(connectorFactory.connect(any(), any())).thenThrow(cause);
        ConnectionException ex = Assertions.assertThrows(ConnectionException.class, factory::createJmxConnection);
        assertThat(ex)
                .hasCause(cause)
                .hasMessageContaining(config.getUrl())
                .hasMessageContaining(config.getInstanceId());
    }
}