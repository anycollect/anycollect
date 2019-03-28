package io.github.anycollect.core.impl.scheduler;

import io.github.anycollect.metric.*;
import io.github.anycollect.metric.noop.NoopMeterRegistry;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

// TODO be more specific, refactor
class MonitoredScheduledThreadPoolExecutorTest {
    private MeterRegistry registry = spy(new NoopMeterRegistry());
    private MonitoredScheduledThreadPoolExecutor executor =
            new MonitoredScheduledThreadPoolExecutor(1, registry, "", Tags.empty());

    @Test
    void metersIsCreated() throws Exception {
        executor.scheduleAtFixedRate(() -> {
            try {
                Thread.sleep(60);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, 0L, 70L, TimeUnit.MILLISECONDS);
        Thread.sleep(1000);
        verify(registry, times(1)).distribution(MeterId.key("scheduler.discrepancy").unit("percents").build());
    }

    @Test
    void cancellationTest() throws Exception {
        ScheduledFuture<?> future = executor.scheduleAtFixedRate(() -> {
            try {
                Thread.sleep(60);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, 0L, 70L, TimeUnit.MILLISECONDS);
        future.cancel(true);
        Thread.sleep(1000);
        verify(registry, times(1)).counter(MeterId.key("scheduler.jobs.failed").build());
    }

    @Test
    void executionExceptionTest() throws Exception {
        ScheduledFuture<?> future = executor.scheduleAtFixedRate(() -> {
            try {
                Thread.sleep(60);
                throw new RuntimeException();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, 0L, 70L, TimeUnit.MILLISECONDS);
        Thread.sleep(1000);
        verify(registry, times(1)).counter(MeterId.key("scheduler.jobs.failed").build());
    }
}