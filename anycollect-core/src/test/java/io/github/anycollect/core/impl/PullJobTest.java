package io.github.anycollect.core.impl;

import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.Metric;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PullJobTest {
    private TestTarget target = mock(TestTarget.class);
    private TestQuery query = new TestQuery("group", "test");
    @SuppressWarnings("unchecked")
    private ResultCallback<TestTarget, TestQuery> callback = (ResultCallback<TestTarget, TestQuery>) mock(ResultCallback.class);
    private Clock clock = mock(Clock.class);
    private PullJob<TestTarget, TestQuery> job;

    @BeforeEach
    void setUp() {
        job = new PullJob<>(
                target,
                query,
                callback,
                clock
        );
    }

    @Test
    void lifecycleTest()  {
        assertThat(job.getState()).isEqualTo(PullJob.State.INITIALIZED);
        Assertions.assertThrows(IllegalStateException.class, () -> job.renew());
        job.submit();
        assertThat(job.getState()).isEqualTo(PullJob.State.WAITING);
        Assertions.assertThrows(IllegalStateException.class, () -> job.renew());
        job.run();
        assertThat(job.getState()).isEqualTo(PullJob.State.COMPLETED);
        job.renew();
        assertThat(job.getState()).isEqualTo(PullJob.State.INITIALIZED);
    }

    @Test
    void successTest() throws ConnectionException, QueryException {
        when(clock.time()).thenReturn(0L, 10L, 25L);
        Metric metric = mock(Metric.class);
        List<Metric> metrics = Collections.singletonList(metric);
        when(target.execute(query)).thenReturn(metrics);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Result<TestTarget, TestQuery>> captor = ArgumentCaptor.forClass(Result.class);
        job.submit();
        job.run();
        verify(callback, times(1)).call(captor.capture());
        Result<TestTarget, TestQuery> result = captor.getValue();
        assertThat(result.getTarget()).isSameAs(target);
        assertThat(result.getQuery()).isSameAs(query);
        assertThat(result.getWaitingTime()).isEqualTo(10L);
        assertThat(result.getProcessingTime()).isEqualTo(15L);
        assertThat(result.getException()).isEmpty();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.isFailed()).isFalse();
        assertThat(result.getMetrics()).isEqualTo(metrics);
    }

    @Test
    void failTest() throws ConnectionException, QueryException {
        when(clock.time()).thenReturn(0L, 5L, 15L);
        QueryException ex = new QueryException();
        when(target.execute(query)).thenThrow(ex);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Result<TestTarget, TestQuery>> captor = ArgumentCaptor.forClass(Result.class);
        job.submit();
        job.run();
        verify(callback, times(1)).call(captor.capture());
        Result<TestTarget, TestQuery> result = captor.getValue();
        assertThat(result.getTarget()).isSameAs(target);
        assertThat(result.getQuery()).isSameAs(query);
        assertThat(result.getWaitingTime()).isEqualTo(5L);
        assertThat(result.getProcessingTime()).isEqualTo(10L);
        assertThat(result.getException()).contains(ex);
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.isFailed()).isTrue();
        assertThat(result.getMetrics()).isSameAs(Collections.emptyList());
    }
}