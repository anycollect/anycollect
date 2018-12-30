package io.github.anycollect.readers.jmx.query;

import io.github.anycollect.readers.jmx.ConnectionException;
import io.github.anycollect.readers.jmx.QueryException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import java.io.IOException;
import java.lang.management.ManagementFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AnyCollectQueryTest {
    private ObjectName objectName;
    private AnyCollectQuery query;

    @BeforeEach
    void setUp() throws Exception {
        objectName = new ObjectName("anycollect:name=test");
        query = new AnyCollectQuery("anycollect", "test", null, objectName);
    }

    @Test
    void ifTargetMBeanDoesNotFitToAnycollectFormatThenQueryExceptionMustBeThrown() throws Exception {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        mBeanServer.registerMBean(new Noop(), objectName);
        Assertions.assertThrows(QueryException.class, () -> query.executeOn(mBeanServer));
    }

    @Test
    void IOExceptionMustBeWrappedToConnectionException() throws IOException {
        MBeanServerConnection connection = mock(MBeanServerConnection.class);
        IOException cause = new IOException("dummy");
        when(connection.queryNames(any(), any())).thenThrow(cause);
        ConnectionException ex = Assertions.assertThrows(ConnectionException.class, () -> query.executeOn(connection));
        assertThat(ex).hasCause(cause);
    }

    public interface NoopMBean {

    }

    public class Noop implements NoopMBean {

    }
}