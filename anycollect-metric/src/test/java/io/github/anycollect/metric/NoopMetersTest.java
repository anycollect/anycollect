package io.github.anycollect.metric;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class NoopMetersTest {
    @Test
    void registryTest() {
        Object stub = new Object();
        NoopMeterRegistry registry = new NoopMeterRegistry();
        assertThat(registry.counter(mock(MeterId.class))).isSameAs(Counter.NOOP);
        assertThat(registry.distribution(mock(MeterId.class))).isSameAs(Distribution.NOOP);
        assertThat(registry.counter(mock(MeterId.class), stub, obj -> 1)).isSameAs(FunctionCounter.NOOP);
        assertThat(registry.gauge(mock(MeterId.class), stub, obj -> 1.0)).isSameAs(Gauge.NOOP);
    }
}