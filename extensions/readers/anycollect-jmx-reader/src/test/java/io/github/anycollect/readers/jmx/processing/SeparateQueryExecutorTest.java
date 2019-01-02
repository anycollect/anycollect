package io.github.anycollect.readers.jmx.processing;

import io.github.anycollect.metric.Metric;
import io.github.anycollect.readers.jmx.application.AllQueryMatcher;
import io.github.anycollect.readers.jmx.application.Application;
import io.github.anycollect.readers.jmx.query.Query;
import io.github.anycollect.readers.jmx.server.JmxConnection;
import io.github.anycollect.readers.jmx.server.Server;
import io.github.anycollect.readers.jmx.server.SimpleServer;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SeparateQueryExecutorTest {
    private CompletableFuture<QueryResult> future = new CompletableFuture<>();
    private ResultCallback callback = future::complete;
    private SeparateQueryExecutor executor = new SeparateQueryExecutor(callback);

    @Test
    void successTest() throws Exception {
        Query query = mock(Query.class);
        Server server = new SimpleServer("1", new Application("app", new AllQueryMatcher(), false), JmxConnection.local());
        List<Metric> metrics = Collections.singletonList(mock(Metric.class));
        when(server.execute(query)).thenReturn(metrics);
        executor.submit(query, server);
        await().atMost(100, TimeUnit.MILLISECONDS)
                .pollInterval(10, TimeUnit.MILLISECONDS)
                .until(future::isDone);
        assertThat(future.get().isSuccess()).isTrue();
        assertThat(future.get().getMetrics()).isEqualTo(metrics);
    }

    @Test
    void failTest() throws Exception {
        SeparateQueryExecutor executor = new SeparateQueryExecutor(callback);
        Query query = mock(Query.class);
        Server server = new SimpleServer("1", new Application("app", new AllQueryMatcher(), false), JmxConnection.local());
        RuntimeException ex = new RuntimeException();
        when(server.execute(query)).thenThrow(ex);
        executor.submit(query, server);
        await().atMost(100, TimeUnit.MILLISECONDS)
                .pollInterval(10, TimeUnit.MILLISECONDS)
                .until(future::isDone);
        assertThat(future.get().isSuccess()).isFalse();
        assertThat(future.get().getException()).contains(ex);
    }
}