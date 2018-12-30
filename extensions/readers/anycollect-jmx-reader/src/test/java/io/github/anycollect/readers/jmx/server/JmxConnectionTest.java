package io.github.anycollect.readers.jmx.server;

import org.junit.jupiter.api.Test;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class JmxConnectionTest {
    @Test
    void closeWithinConnectorTest() throws IOException {
        JMXConnector connector = mock(JMXConnector.class);
        MBeanServerConnection connection = mock(MBeanServerConnection.class);
        JmxConnection jmxConnection = new JmxConnection(connector, connection);
        assertThat(jmxConnection.isClosed()).isFalse();
        jmxConnection.close();
        assertThat(jmxConnection.isClosed()).isTrue();
        jmxConnection.close();
        assertThat(jmxConnection.isClosed()).isTrue();
        verify(connector, times(1)).close();
    }

    @Test
    void closeWithoutConnectorTest() throws IOException {
        MBeanServerConnection connection = mock(MBeanServerConnection.class);
        JmxConnection jmxConnection = new JmxConnection(null, connection);
        assertThat(jmxConnection.isClosed()).isFalse();
        jmxConnection.close();
        assertThat(jmxConnection.isClosed()).isTrue();
        jmxConnection.close();
        assertThat(jmxConnection.isClosed()).isTrue();
    }

    @Test
    void destroyTest() {
        JmxConnection connection = JmxConnection.local();
        connection.markAsDestroyed();
        assertThat(connection.isAlive()).isFalse();
    }
}