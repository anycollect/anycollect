package io.github.anycollect.core.impl.scheduler;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class MonitoredScheduledThreadPoolExecutorTest {
    private SimpleMeterRegistry registry = new SimpleMeterRegistry();
    private MonitoredScheduledThreadPoolExecutor executor =
            new MonitoredScheduledThreadPoolExecutor(1, registry);

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
        assertThat(registry.summary("scheduler.discrepancy").takeSnapshot().count()).isNotZero();
        assertThat(registry.counter("scheduler.processing.time").count()).isNotZero();
        assertThat(registry.counter("scheduler.failed.jobs").count()).isZero();
        assertThat(registry.counter("scheduler.succeeded.jobs").count()).isNotZero();
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
        assertThat(registry.counter("scheduler.failed.jobs").count()).isNotZero();
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
        assertThat(registry.counter("scheduler.failed.jobs").count()).isNotZero();
    }
}