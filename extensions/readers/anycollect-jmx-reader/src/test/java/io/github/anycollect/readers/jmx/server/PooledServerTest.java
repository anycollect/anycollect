package io.github.anycollect.readers.jmx.server;

import io.github.anycollect.readers.jmx.ConnectionException;
import io.github.anycollect.readers.jmx.QueryException;
import io.github.anycollect.readers.jmx.application.Application;
import io.github.anycollect.readers.jmx.application.SimpleQueryMatcher;
import io.github.anycollect.readers.jmx.query.NoopQuery;
import io.github.anycollect.readers.jmx.query.Query;
import io.github.anycollect.readers.jmx.server.pool.JmxConnectionPool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.management.MBeanServerConnection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PooledServerTest {
    private Application dummy = new Application("dummy",
            new SimpleQueryMatcher("group", "label"),
            null,
            null,
            false);
    private JmxConnectionPool pool = mock(JmxConnectionPool.class);

    @Test
    void mustCheckQueryBeforeExecute() {
        Server server = new PooledServer("dummy-server", dummy, pool);
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class,
                () -> server.execute(new NoopQuery("group", "wrong")));
        assertThat(ex).hasMessageContaining("query");
    }

    @Test
    void mustBorrowAndThenReturnConnectionToPool() throws ConnectionException, QueryException {
        Server server = new PooledServer("dummy-server", dummy, pool);
        JmxConnection jmxConnection = mock(JmxConnection.class);
        when(pool.borrowConnection()).thenReturn(jmxConnection);
        server.execute(new NoopQuery("group", "label"));
        verify(pool, times(1)).returnConnection(jmxConnection);
    }

    @Test
    void mustInvalidateConnectionIfExceptionDuringExecution() throws ConnectionException, QueryException {
        Server server = new PooledServer("dummy-server", dummy, pool);
        MBeanServerConnection serverConnection = mock(MBeanServerConnection.class);
        JmxConnection jmxConnection = new JmxConnection(null, serverConnection);
        when(pool.borrowConnection()).thenReturn(jmxConnection);
        Query query = spy(new NoopQuery("group", "label"));
        when(query.executeOn(any())).thenThrow(new ConnectionException("timeout"));
        ConnectionException ex = Assertions.assertThrows(ConnectionException.class, () -> server.execute(query));
        assertThat(ex).hasMessage("timeout");
        verify(pool, times(1)).invalidateConnection(jmxConnection);
    }

    @Test
    void mustForwardBusinessExceptionFromPool() throws ConnectionException {
        Server server = new PooledServer("dummy-server", dummy, pool);
        when(pool.borrowConnection()).thenThrow(new ConnectionException("dummy"));
        ConnectionException ex = Assertions.assertThrows(ConnectionException.class, () -> server.execute(new NoopQuery("group", "label")));
        assertThat(ex).hasMessage("dummy");
    }

    @Test
    void mustForwardBusinessExceptionFromQuery() throws ConnectionException, QueryException {
        Server server = new PooledServer("dummy-server", dummy, pool);
        NoopQuery query = spy(new NoopQuery("group", "label"));
        when(pool.borrowConnection()).thenReturn(new JmxConnection(null, mock(MBeanServerConnection.class)));
        when(query.executeOn(any())).thenThrow(new QueryException("dummy"));
        QueryException ex = Assertions.assertThrows(QueryException.class, () -> server.execute(query));
        assertThat(ex).hasMessage("dummy");
    }

    @Test
    void propertiesTest() {
        Server server = new PooledServer("dummy-server", dummy, pool);
        assertThat(server.getId()).isEqualTo("dummy-server");
        assertThat(server.getApplication()).isSameAs(dummy);
    }
}