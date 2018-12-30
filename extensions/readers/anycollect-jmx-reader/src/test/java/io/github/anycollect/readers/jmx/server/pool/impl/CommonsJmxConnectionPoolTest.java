package io.github.anycollect.readers.jmx.server.pool.impl;

import io.github.anycollect.readers.jmx.ConnectionException;
import io.github.anycollect.readers.jmx.server.JmxConnection;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CommonsJmxConnectionPoolTest {
    @SuppressWarnings("unchecked")
    private GenericObjectPool<JmxConnection> mockPool = (GenericObjectPool<JmxConnection>) mock(GenericObjectPool.class);
    private CommonsJmxConnectionPool pool = new CommonsJmxConnectionPool(mockPool);

    @Test
    void poolMustForwardBusinessExceptionsFromFactory() throws Exception {
        when(mockPool.borrowObject()).thenThrow(new ConnectionException("test"));
        ConnectionException ex = Assertions.assertThrows(ConnectionException.class, pool::borrowConnection);
        assertThat(ex).hasMessage("test");
    }

    @Test
    void ifPoolIsExhaustedThenBusinessExceptionMustBeThrown() throws Exception {
        when(mockPool.borrowObject()).thenThrow(new NoSuchElementException());
        ConnectionException ex = Assertions.assertThrows(ConnectionException.class, pool::borrowConnection);
        assertThat(ex).hasMessageContaining("unable to borrow jmx connection");
    }

    @Test
    void mustForwardInvocations() throws Exception {
        JmxConnection jmxConnection = JmxConnection.local();
        when(mockPool.borrowObject()).thenReturn(jmxConnection);
        pool.borrowConnection();
        verify(mockPool, times(1)).borrowObject();
        pool.invalidateConnection(jmxConnection);
        verify(mockPool, times(1)).invalidateObject(jmxConnection);
        pool.returnConnection(jmxConnection);
        verify(mockPool, times(1)).returnObject(jmxConnection);
    }

    @Test
    void mustHandleUnexpectedExceptions() throws Exception {
        doThrow(NullPointerException.class).when(mockPool).invalidateObject(any());
        doThrow(NullPointerException.class).when(mockPool).returnObject(any());
        doThrow(NullPointerException.class).when(mockPool).borrowObject();
        JmxConnection connection = mock(JmxConnection.class);
        Assertions.assertDoesNotThrow(() -> pool.invalidateConnection(connection));
        Assertions.assertDoesNotThrow(() -> pool.returnConnection(connection));
        Assertions.assertThrows(ConnectionException.class, () -> pool.borrowConnection());
    }
}