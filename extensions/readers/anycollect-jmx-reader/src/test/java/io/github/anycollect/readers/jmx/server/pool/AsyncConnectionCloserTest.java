package io.github.anycollect.readers.jmx.server.pool;

import io.github.anycollect.readers.jmx.server.JmxConnection;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

class AsyncConnectionCloserTest {
    @Test
    void connectionMustBeClosedOnceAndAsync() {
        ExecutorService service = mock(ExecutorService.class);
        AsyncConnectionCloser closer = new AsyncConnectionCloser(service);
        JmxConnection jmxConnection = spy(new JmxConnection(null, mock(MBeanServerConnection.class)));
        ArgumentCaptor<Runnable> argument = ArgumentCaptor.forClass(Runnable.class);
        closer.closeAsync(jmxConnection);
        verify(service, times(1)).submit(argument.capture());
        Runnable closeJob = argument.getValue();
        assertThat(jmxConnection.isClosed()).isFalse();
        closeJob.run();
        assertThat(jmxConnection.isClosed()).isTrue();
    }

    @Test
    void nothingBadShouldHappenIfConnectionCannotBeClosed() throws Exception {
        ThreadPoolExecutor service = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        AsyncConnectionCloser closer = new AsyncConnectionCloser(service);
        JMXConnector connector = mock(JMXConnector.class);
        doThrow(new IOException()).when(connector).close();
        closer.closeAsync(new JmxConnection(connector, mock(MBeanServerConnection.class)));
        await().atMost(1, TimeUnit.SECONDS).until(() -> service.getQueue().isEmpty());
        verify(connector, times(1)).close();
    }
}