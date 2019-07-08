package io.github.anycollect.core.impl.pull.availability;

import io.github.anycollect.assertj.MetricsAssert;
import io.github.anycollect.core.api.dispatcher.Accumulator;
import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.impl.TestTarget;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HealthCheckTest {
    private final Tags expectedTags = Tags.of("tk1", "tv1",
            "k1", "v1");
    private final Tags expectedMeta = Tags.of("target.id", "t1",
            "tm1", "tv2",
            "m1", "v2");

    @ParameterizedTest
    @MethodSource("arguments")
    void metricsTest(long now, long timeout, Check check, int status, int up, int down, int unknown) {
        Accumulator accumulator = Dispatcher.accumulator();
        TestTarget target = mock(TestTarget.class);
        CheckingTarget<TestTarget> checkingTarget = new CheckingTarget<>(
                target,
                0
        );
        when(target.getId()).thenReturn("t1");
        when(target.getTags()).thenReturn(Tags.of("tk1", "tv1"));
        when(target.getMeta()).thenReturn(Tags.of("tm1", "tv2"));
        Clock clock = mock(Clock.class);
        HealthCheck healthCheck = new HealthCheck(
                accumulator,
                checkingTarget,
                Tags.of("k1", "v1"),
                Tags.of("m1", "v2"),
                timeout,
                clock
        );
        when(clock.wallTime()).thenReturn(now);
        checkingTarget.update(check);
        healthCheck.run();
        List<Metric> metrics = accumulator.purge();
        assertMetric(metrics, "health.check", status);
        assertMetric(metrics,"instances.up", up);
        assertMetric(metrics,"instances.down", down);
        assertMetric(metrics,"instances.unknown", unknown);
    }

    private static Stream<Arguments> arguments() {
        return Stream.of(
                Arguments.of(100, 50, Check.passed(50), Health.PASSED.getStatusCode(), 1, 0, 0),
                Arguments.of(100, 50, Check.passed(49), Health.UNKNOWN.getStatusCode(), 0, 0, 1),
                Arguments.of(100, 50, Check.failed(50), Health.FAILED.getStatusCode(), 0, 1, 0),
                Arguments.of(100, 50, Check.failed(49), Health.UNKNOWN.getStatusCode(), 0, 0, 1),
                Arguments.of(100, 50, Check.unknown(50), Health.UNKNOWN.getStatusCode(), 0, 0, 1),
                Arguments.of(100, 50, Check.unknown(49), Health.UNKNOWN.getStatusCode(), 0, 0, 1)
        );
    }

    private void assertMetric(List<Metric> metrics, String key, double value) {
        MetricsAssert.assertThat(metrics).contains(key, expectedTags, expectedMeta).hasValue(value);
    }
}