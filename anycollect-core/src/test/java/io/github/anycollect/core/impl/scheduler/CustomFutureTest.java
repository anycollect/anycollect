package io.github.anycollect.core.impl.scheduler;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Delayed;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

class CustomFutureTest {
    @Test
    void delegationTest() throws Exception {
        RunnableScheduledFuture<?> delegate = mock(RunnableScheduledFuture.class);
        CustomFuture<?> future = new CustomFuture<>(delegate);

        future.cancel(false);
        future.get();
        future.get(10, TimeUnit.MILLISECONDS);
        Delayed delayed = mock(Delayed.class);
        future.compareTo(delayed);
        future.isPeriodic();
        future.isCancelled();
        future.isDone();

        verify(delegate, times(1)).cancel(false);
        verify(delegate, times(1)).get();
        verify(delegate, times(1)).get(10, TimeUnit.MILLISECONDS);
        verify(delegate, times(1)).compareTo(delayed);
        verify(delegate, times(1)).isPeriodic();
        verify(delegate, times(1)).isCancelled();
        verify(delegate, times(1)).isDone();
    }
}