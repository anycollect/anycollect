package io.github.anycollect.readers.jmx.discovery;

import io.github.anycollect.readers.jmx.ConnectionException;
import io.github.anycollect.readers.jmx.QueryException;
import io.github.anycollect.readers.jmx.application.Application;
import io.github.anycollect.readers.jmx.application.ApplicationRegistry;
import io.github.anycollect.readers.jmx.application.SimpleQueryMatcher;
import io.github.anycollect.readers.jmx.query.NoopQuery;
import io.github.anycollect.readers.jmx.query.Query;
import io.github.anycollect.readers.jmx.server.Server;
import io.github.anycollect.readers.jmx.server.pool.JmxConnectionPoolFactory;
import io.github.anycollect.readers.jmx.server.pool.MockJmxConnectionPoolFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CurrentApplicationServerDiscoveryTest {
    @Test
    void registryMustContainCurrentApplication() {
        JmxConnectionPoolFactory poolFactory = mock(JmxConnectionPoolFactory.class);
        CurrentApplicationServerDiscovery discovery = new CurrentApplicationServerDiscovery("dummy", poolFactory);
        DiscoverException ex = Assertions.assertThrows(DiscoverException.class, () -> discovery.getServers(ApplicationRegistry.empty()));
        assertThat(ex).hasMessageContaining("dummy");
    }

    @Test
    void connectionMustBeOfTypeMBeanServer() throws DiscoverException, QueryException, ConnectionException {
        Application dummy = new Application("dummy", new SimpleQueryMatcher("group", "label"), false);
        ApplicationRegistry registry = ApplicationRegistry.singleton(dummy);
        JmxConnectionPoolFactory poolFactory = new MockJmxConnectionPoolFactory();
        Query query = spy(new NoopQuery("group", "label"));
        when(query.executeOn(any())).thenReturn(Collections.emptyList());
        CurrentApplicationServerDiscovery discovery = new CurrentApplicationServerDiscovery("dummy", poolFactory);

        Server server = discovery.getServer(registry);
        server.execute(query);

        ArgumentCaptor<MBeanServerConnection> mbeanServer = ArgumentCaptor.forClass(MBeanServerConnection.class);
        verify(query, times(1)).executeOn(mbeanServer.capture());
        MBeanServerConnection connection = mbeanServer.getValue();
        assertThat(connection).isInstanceOf(MBeanServer.class);
    }
}