package io.github.anycollect.meter.api;

import io.github.anycollect.metric.Key;
import io.github.anycollect.metric.Tags;
import org.junit.jupiter.api.Test;

import java.util.function.ToDoubleFunction;

import static org.mockito.Mockito.*;

class GaugeTest {
    @Test
    void registerGauge() {
        Object obj = new Object();
        ToDoubleFunction<Object> value = o -> 1.0;
        MeterRegistry registry = mock(MeterRegistry.class);
        Gauge.make("test", obj, value)
                .unit("tests")
                .register(registry);
        verify(registry, times(1)).gauge(
                new ImmutableMeterId(Key.of("test"), "tests", Tags.empty(), Tags.empty()),
                obj,
                value
        );
    }
}