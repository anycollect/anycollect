package io.github.anycollect.metric;

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
                MeterId.key("test").unit("tests").build(),
                obj,
                value
        );
    }
}