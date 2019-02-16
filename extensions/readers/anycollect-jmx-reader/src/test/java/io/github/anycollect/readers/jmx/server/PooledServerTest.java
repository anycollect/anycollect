package io.github.anycollect.readers.jmx.server;

import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.readers.jmx.query.JmxQuery;
import io.github.anycollect.readers.jmx.query.NoopQuery;
import io.github.anycollect.readers.jmx.server.pool.JmxConnectionPool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.management.MBeanServerConnection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PooledServerTest {
    private JmxConnectionPool pool = mock(JmxConnectionPool.class);

    @Test
    void mustBorrowAndThenReturnConnectionToPool() throws ConnectionException, QueryException {
        JavaApp server = new PooledJavaApp("dummy-server", pool);
        JmxConnection jmxConnection = mock(JmxConnection.class);
        when(pool.borrowConnection()).thenReturn(jmxConnection);
        server.execute(new NoopQuery("id"));
        verify(pool, times(1)).returnConnection(jmxConnection);
    }

    @Test
    void mustInvalidateConnectionIfExceptionDuringExecution() throws ConnectionException, QueryException {
        JavaApp server = new PooledJavaApp("dummy-server", pool);
        MBeanServerConnection serverConnection = mock(MBeanServerConnection.class);
        JmxConnection jmxConnection = new JmxConnection(null, serverConnection);
        when(pool.borrowConnection()).thenReturn(jmxConnection);
        JmxQuery query = spy(new NoopQuery("id"));
        when(query.executeOn(any())).thenThrow(new ConnectionException("timeout"));
        ConnectionException ex = Assertions.assertThrows(ConnectionException.class, () -> server.execute(query));
        assertThat(ex).hasMessage("timeout");
        verify(pool, times(1)).invalidateConnection(jmxConnection);
    }

    @Test
    void mustForwardBusinessExceptionFromPool() throws ConnectionException {
        JavaApp server = new PooledJavaApp("dummy-server", pool);
        when(pool.borrowConnection()).thenThrow(new ConnectionException("dummy"));
        ConnectionException ex = Assertions.assertThrows(ConnectionException.class, () -> server.execute(new NoopQuery("id")));
        assertThat(ex).hasMessage("dummy");
    }

    @Test
    void mustForwardBusinessExceptionFromQuery() throws ConnectionException, QueryException {
        JavaApp server = new PooledJavaApp("dummy-server", pool);
        NoopQuery query = spy(new NoopQuery("id"));
        when(pool.borrowConnection()).thenReturn(new JmxConnection(null, mock(MBeanServerConnection.class)));
        when(query.executeOn(any())).thenThrow(new QueryException("dummy"));
        QueryException ex = Assertions.assertThrows(QueryException.class, () -> server.execute(query));
        assertThat(ex).hasMessage("dummy");
    }

    @Test
    void propertiesTest() {
        JavaApp server = new PooledJavaApp("dummy-server", pool);
        assertThat(server.getId()).isEqualTo("dummy-server");
    }
}