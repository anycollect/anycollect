package io.github.anycollect.readers.jmx.processing;

import io.github.anycollect.readers.jmx.query.Query;
import io.github.anycollect.readers.jmx.server.Server;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class QuerySubmitJobTest {
    @Test
    void mustSubmitAsyncTaskToExecutor() {
        QueryExecutor executor = mock(QueryExecutor.class);
        Query query = mock(Query.class);
        Server server = mock(Server.class);
        QuerySubmitJob job = new QuerySubmitJob(query, server, executor);
        job.run();
        verify(executor, times(1)).submit(query, server);
    }
}