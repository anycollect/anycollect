package io.github.anycollect.core.impl;

import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.Metric;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SubmitJobTest {
    @Test
    void mustReuseJobAfterCompletion() throws ConnectionException, QueryException {
        TestTarget target = mock(TestTarget.class);
        TestQuery query = mock(TestQuery.class);
        when(target.execute(query)).then((Answer<List<Metric>>) invocation -> {
            Thread.sleep(1000);
            return Collections.emptyList();
        });
        Puller puller = mock(Puller.class);
        doAnswer(invocation -> {
            PullJob job = invocation.getArgument(0);
            job.run();
            return null;
        }).when(puller).pullAsync(any());
        SubmitJob job = new SubmitJob(
                target,
                query,
                Duration.ZERO,
                puller,
                ResultCallback.noop(),
                Clock.getDefault()
        );
        @SuppressWarnings("unchecked")
        ArgumentCaptor<PullJob<TestTarget, TestQuery>> captor = ArgumentCaptor.forClass(PullJob.class);
        job.run();
        verify(puller, times(1)).pullAsync(captor.capture());
        PullJob<TestTarget, TestQuery> job1 = captor.getValue();
        assertThat(job1.getState()).isEqualTo(PullJob.State.COMPLETED);
        job.run();
        verify(puller, times(2)).pullAsync(captor.capture());
        PullJob<TestTarget, TestQuery> job2 = captor.getValue();
        assertThat(job1).isSameAs(job2);
        // should not pass job because it's not completed
        job2.submit();
        job.run();
        verify(puller, times(2)).pullAsync(job2);
    }
}