package io.github.anycollect.metric.noop;

import io.github.anycollect.metric.MeterId;
import org.junit.jupiter.api.Test;

import java.util.function.ToDoubleFunction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class NoopMetersTest {
    @Test
    void counterTest() {
        MeterId id = mock(MeterId.class);
        NoopCounter counter = new NoopCounter(id);
        counter.increment(10);
        assertThat(counter.getId()).isSameAs(id);
    }

    @Test
    void distributionTest() {
        MeterId id = mock(MeterId.class);
        NoopDistribution distribution = new NoopDistribution(id);
        distribution.record(10);
        assertThat(distribution.getId()).isSameAs(id);
    }

    @Test
    void gaugeTest() {
        MeterId id = mock(MeterId.class);
        NoopGauge gauge = new NoopGauge(id);
        assertThat(gauge.getId()).isSameAs(id);
    }

    @Test
    void functionCounter() {
        MeterId id = mock(MeterId.class);
        NoopFunctionCounter counter = new NoopFunctionCounter(id);
        assertThat(counter.getId()).isSameAs(id);
    }

    @Test
    void registryTest() {
        Object stub = new Object();
        ToDoubleFunction<Object> value = obj -> 1.0;
        NoopMeterRegistry registry = new NoopMeterRegistry();
        assertThat(registry.counter(mock(MeterId.class))).isInstanceOf(NoopCounter.class);
        assertThat(registry.distribution(mock(MeterId.class))).isInstanceOf(NoopDistribution.class);
        assertThat(registry.counter(mock(MeterId.class), stub, value)).isInstanceOf(NoopFunctionCounter.class);
        assertThat(registry.gauge(mock(MeterId.class), stub, value)).isInstanceOf(NoopGauge.class);
    }
}