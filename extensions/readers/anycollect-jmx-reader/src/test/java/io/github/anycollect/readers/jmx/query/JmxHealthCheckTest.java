package io.github.anycollect.readers.jmx.query;

import io.github.anycollect.core.exceptions.ConnectionException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.management.MBeanServerConnection;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JmxHealthCheckTest {
    @Test
    void mustThrowConnectionExceptionIfUnableToQuery() throws Exception {
        JmxHealthCheck check = new JmxHealthCheck();
        MBeanServerConnection connection = mock(MBeanServerConnection.class);
        when(connection.getDefaultDomain()).thenThrow(IOException.class);
        Assertions.assertThatThrownBy(() -> check.executeOn(connection, new MockJavaApp()))
                .isInstanceOf(ConnectionException.class);
    }

    @Test
    void mustReturnEmptyListIfOk() throws Exception {
        JmxHealthCheck check = new JmxHealthCheck();
        MBeanServerConnection connection = mock(MBeanServerConnection.class);
        when(connection.getDefaultDomain()).thenReturn("test");
        Assertions.assertThat(check.executeOn(connection, new MockJavaApp())).isEmpty();
    }
}