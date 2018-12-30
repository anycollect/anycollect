package io.github.anycollect.readers.jmx.server.pool.impl;

import io.github.anycollect.readers.jmx.server.JmxConnection;
import io.github.anycollect.readers.jmx.server.JmxConnectionFactory;
import io.github.anycollect.readers.jmx.server.pool.AsyncConnectionCloser;
import io.github.anycollect.readers.jmx.server.pool.JmxConnectionPool;
import org.junit.jupiter.api.Test;

import javax.management.MBeanServerConnection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class CommonsJmxConnectionPoolFactoryTest {
    @Test
    void poolMustWrapGivenFactory() throws Exception {
        AsyncConnectionCloser closer = mock(AsyncConnectionCloser.class);
        CommonsJmxConnectionPoolFactory factory = new CommonsJmxConnectionPoolFactory(closer);
        JmxConnection jmxConnection = spy(new JmxConnection(null, mock(MBeanServerConnection.class)));
        JmxConnectionFactory jmxConnectionFactory = mock(JmxConnectionFactory.class);
        when(jmxConnectionFactory.createJmxConnection()).thenReturn(jmxConnection);

        JmxConnectionPool pool = factory.create(jmxConnectionFactory);
        JmxConnection actualConnection = pool.borrowConnection();

        assertThat(actualConnection).isSameAs(jmxConnection);
    }
}