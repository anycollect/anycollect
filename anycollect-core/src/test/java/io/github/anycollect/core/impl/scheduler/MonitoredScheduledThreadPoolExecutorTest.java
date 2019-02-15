package io.github.anycollect.core.impl.scheduler;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.metric.*;
import io.github.anycollect.micrometer.Config;
import io.github.anycollect.micrometer.MicrometerMeterRegistry;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

// TODO be more specific, refactor
class MonitoredScheduledThreadPoolExecutorTest {
    private MeterRegistry registry = new MicrometerMeterRegistry(mock(Dispatcher.class), Clock.getDefault(), (Config) key -> null);
    private MonitoredScheduledThreadPoolExecutor executor =
            new MonitoredScheduledThreadPoolExecutor(1, registry, Tags.empty());

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
        Distribution summary = registry.distribution(MeterId.key("scheduler.discrepancy").unit("percentage").build());
        assertThat(summary.measure().map(Metric::getId)).contains(
                MetricId.key("scheduler.discrepancy")
                        .unit("percentage")
                        .type(Type.GAUGE)
                        .stat(Stat.MEAN)
                        .build()
        );
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
        Counter failedJobs = registry.counter(MeterId.key("scheduler.failed.jobs").unit("job").build());
        assertThat(failedJobs.measure().map(Metric::getId)).contains(
                MetricId.key("scheduler.failed.jobs")
                        .unit("job")
                        .stat(Stat.value())
                        .type(Type.COUNTER)
                        .build()
        );
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
        Counter failedJobs = registry.counter(MeterId.key("scheduler.failed.jobs").unit("job").build());
        assertThat(failedJobs.measure().map(Metric::getId)).contains(
                MetricId.key("scheduler.failed.jobs")
                        .unit("job")
                        .stat(Stat.value())
                        .type(Type.COUNTER)
                        .build()
        );
    }
}