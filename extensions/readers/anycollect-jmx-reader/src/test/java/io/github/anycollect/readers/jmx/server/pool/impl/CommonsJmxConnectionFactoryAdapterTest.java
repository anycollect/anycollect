package io.github.anycollect.readers.jmx.server.pool.impl;

import io.github.anycollect.readers.jmx.server.JmxConnection;
import io.github.anycollect.readers.jmx.server.JmxConnectionFactory;
import io.github.anycollect.readers.jmx.server.pool.AsyncConnectionCloser;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class CommonsJmxConnectionFactoryAdapterTest {
    @Test
    void destroyConnectionTest() {
        JmxConnectionFactory factory = mock(JmxConnectionFactory.class);
        AsyncConnectionCloser closer = mock(AsyncConnectionCloser.class);
        CommonsJmxConnectionFactoryAdapter adapter = new CommonsJmxConnectionFactoryAdapter(factory, closer);
        JmxConnection jmxConnection = mock(JmxConnection.class);
        adapter.destroyObject(new DefaultPooledObject<>(jmxConnection));
        verify(closer, times(1)).closeAsync(any());
        verify(jmxConnection, times(1)).markAsDestroyed();
        assertThat(jmxConnection.isAlive()).isFalse();
    }
}