package io.github.anycollect.readers.jmx.discovery;

import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.noop.NoopMeterRegistry;
import io.github.anycollect.readers.jmx.query.JmxQuery;
import io.github.anycollect.readers.jmx.query.NoopQuery;
import io.github.anycollect.readers.jmx.server.JavaApp;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CurrentAppTest {
    @Test
    void connectionMustBeOfTypeMBeanServer() throws ConnectionException, QueryException {
        JmxQuery query = spy(new NoopQuery("id"));
        when(query.executeOn(any(), any())).thenReturn(Collections.emptyList());
        CurrentApp discovery = new CurrentApp(new CurrentApp.Config(new NoopMeterRegistry(), "dummy"));

        JavaApp server = discovery.discover().iterator().next();
        server.execute(query);

        ArgumentCaptor<MBeanServerConnection> mbeanServer = ArgumentCaptor.forClass(MBeanServerConnection.class);
        verify(query, times(1)).executeOn(mbeanServer.capture(), any());
        MBeanServerConnection connection = mbeanServer.getValue();
        assertThat(connection).isInstanceOf(MBeanServer.class);
    }
}