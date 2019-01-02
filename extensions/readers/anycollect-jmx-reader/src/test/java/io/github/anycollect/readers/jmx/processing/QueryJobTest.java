package io.github.anycollect.readers.jmx.processing;

import io.github.anycollect.metric.Metric;
import io.github.anycollect.readers.jmx.query.Query;
import io.github.anycollect.readers.jmx.server.Server;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class QueryJobTest {
    private Server server = mock(Server.class);
    private Query query = mock(Query.class);

    @Test
    void mustQueryOnTheServer() throws Exception {
        QueryJob job = new QueryJob(query, server, ResultCallback.noop());
        job.run();
        verify(server, times(1)).execute(query);
    }

    @Test
    void failResultIfExceptionDuringQuerying() throws Exception {
        ResultCallback callback = mock(ResultCallback.class);
        RuntimeException ex = new RuntimeException();
        when(server.execute(query)).thenThrow(ex);
        QueryJob job = new QueryJob(query, server, callback);
        job.run();
        ArgumentCaptor<QueryResult> captor = ArgumentCaptor.forClass(QueryResult.class);
        verify(callback, times(1)).call(captor.capture());
        QueryResult result = captor.getValue();
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getException()).contains(ex);
        Assertions.assertThrows(IllegalStateException.class, result::getMetrics);
        assertThat(result.getQuery()).isSameAs(query);
        assertThat(result.getServer()).isSameAs(server);
        assertThat(result.getDuration()).isNotNull();
    }

    @Test
    void successResultTest() throws Exception {
        ResultCallback callback = mock(ResultCallback.class);
        List<Metric> metrics = Collections.emptyList();
        when(server.execute(query)).thenReturn(metrics);
        QueryJob job = new QueryJob(query, server, callback);
        job.run();
        ArgumentCaptor<QueryResult> captor = ArgumentCaptor.forClass(QueryResult.class);
        verify(callback, times(1)).call(captor.capture());
        QueryResult result = captor.getValue();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getMetrics()).isEqualTo(metrics);
        assertThat(result.getException()).isEmpty();
        assertThat(result.getQuery()).isSameAs(query);
        assertThat(result.getServer()).isSameAs(server);
        assertThat(result.getDuration()).isNotNull();
    }
}