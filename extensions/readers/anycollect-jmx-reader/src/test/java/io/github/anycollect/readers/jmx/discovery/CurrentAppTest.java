package io.github.anycollect.readers.jmx.discovery;

import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.readers.jmx.query.JmxQuery;
import io.github.anycollect.readers.jmx.query.NoopQuery;
import io.github.anycollect.readers.jmx.server.JavaApp;
import io.github.anycollect.readers.jmx.server.pool.JmxConnectionPoolFactory;
import io.github.anycollect.readers.jmx.server.pool.MockJmxConnectionPoolFactory;
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
        JmxConnectionPoolFactory poolFactory = new MockJmxConnectionPoolFactory();
        JmxQuery query = spy(new NoopQuery("group", "label"));
        when(query.executeOn(any())).thenReturn(Collections.emptyList());
        CurrentApp discovery = new CurrentApp("dummy", poolFactory);

        JavaApp server = discovery.discover().iterator().next();
        server.execute(query);

        ArgumentCaptor<MBeanServerConnection> mbeanServer = ArgumentCaptor.forClass(MBeanServerConnection.class);
        verify(query, times(1)).executeOn(mbeanServer.capture());
        MBeanServerConnection connection = mbeanServer.getValue();
        assertThat(connection).isInstanceOf(MBeanServer.class);
    }
}