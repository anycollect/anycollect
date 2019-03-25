package io.github.anycollect.readers.jmx.discovery;

import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.metric.noop.NoopMeterRegistry;
import io.github.anycollect.readers.jmx.server.JavaApp;
import io.github.anycollect.readers.jmx.query.operations.QueryOperation;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CurrentAppTest {
    @Test
    void connectionMustBeOfTypeMBeanServer() throws ConnectionException, QueryException {
        CurrentApp discovery = new CurrentApp(new NoopMeterRegistry(), new CurrentApp.Config("dummy", Tags.empty(), Tags.empty()));

        JavaApp server = discovery.discover().iterator().next();
        @SuppressWarnings("unchecked")
        QueryOperation<Object> operation = mock(QueryOperation.class);
        server.operate(operation);

        ArgumentCaptor<MBeanServerConnection> mbeanServer = ArgumentCaptor.forClass(MBeanServerConnection.class);
        verify(operation, times(1)).operate(mbeanServer.capture());
        MBeanServerConnection connection = mbeanServer.getValue();
        assertThat(connection).isInstanceOf(MBeanServer.class);
    }
}