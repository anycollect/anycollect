package io.github.anycollect.readers.jmx.server;

import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
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
        server.operate(connection -> null);
        verify(pool, times(1)).returnConnection(jmxConnection);
    }

    @Test
    void mustInvalidateConnectionIfExceptionDuringExecution() throws ConnectionException, QueryException {
        JavaApp server = new PooledJavaApp("dummy-server", pool);
        MBeanServerConnection serverConnection = mock(MBeanServerConnection.class);
        JmxConnection jmxConnection = new JmxConnection(null, serverConnection);
        when(pool.borrowConnection()).thenReturn(jmxConnection);
        ConnectionException ex = Assertions.assertThrows(ConnectionException.class, () -> server.operate(connection -> {
            throw new ConnectionException("timeout");
        }));
        assertThat(ex).hasMessage("timeout");
        verify(pool, times(1)).invalidateConnection(jmxConnection);
    }

    @Test
    void mustForwardBusinessExceptionFromPool() throws ConnectionException {
        JavaApp server = new PooledJavaApp("dummy-server", pool);
        when(pool.borrowConnection()).thenThrow(new ConnectionException("dummy"));
        ConnectionException ex = Assertions.assertThrows(ConnectionException.class, () -> server.operate(connection -> null));
        assertThat(ex).hasMessage("dummy");
    }

    @Test
    void mustForwardBusinessExceptionFromOperation() throws ConnectionException, QueryException {
        JavaApp server = new PooledJavaApp("dummy-server", pool);
        when(pool.borrowConnection()).thenReturn(new JmxConnection(null, mock(MBeanServerConnection.class)));
        QueryException ex = Assertions.assertThrows(QueryException.class, () -> server.operate(connection -> {
            throw new QueryException("dummy");
        }));
        assertThat(ex).hasMessage("dummy");
    }

    @Test
    void propertiesTest() {
        JavaApp server = new PooledJavaApp("dummy-server", pool);
        assertThat(server.getId()).isEqualTo("dummy-server");
    }
}