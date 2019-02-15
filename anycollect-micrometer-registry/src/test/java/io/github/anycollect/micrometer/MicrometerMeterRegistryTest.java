package io.github.anycollect.micrometer;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.metric.*;
import io.micrometer.core.instrument.push.PushRegistryConfig;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class MicrometerMeterRegistryTest {
    @Test
    void summaryTest() {
        Dispatcher dispatcher = mock(Dispatcher.class);
        MicrometerMeterRegistry registry = new MicrometerMeterRegistry(dispatcher, Clock.getDefault(), new PushRegistryConfig() {
            @Override
            public String prefix() {
                return "anycollect";
            }

            @Override
            public String get(String key) {
                return null;
            }
        });
        Distribution summary = Distribution.key("test")
                .unit("tests")
                .meta("plugin", "jmx")
                .register(registry);

        assertThat(summary.measure().map(Metric::getId))
                .containsExactlyInAnyOrder(
                        MetricId.builder().key("test").unit("tests").type(Type.GAUGE).stat(Stat.percentile(50)).build(),
                        MetricId.builder().key("test").unit("tests").type(Type.GAUGE).stat(Stat.percentile(75)).build(),
                        MetricId.builder().key("test").unit("tests").type(Type.GAUGE).stat(Stat.percentile(99)).build(),
                        MetricId.builder().key("test").unit("tests").type(Type.GAUGE).stat(Stat.percentile(999)).build(),
                        MetricId.builder().key("test").unit("tests").type(Type.GAUGE).stat(Stat.mean()).build(),
                        MetricId.builder().key("test").unit("tests").type(Type.GAUGE).stat(Stat.max()).build()
                );
    }
}