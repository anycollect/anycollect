package io.github.anycollect.metric.noop;

import io.github.anycollect.metric.MeterId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class NoopMetersTest {
    @Test
    void counterTest() {
        MeterId id = mock(MeterId.class);
        NoopCounter counter = new NoopCounter(id);
        counter.increment(10);
        assertThat(counter.measure().isEmpty()).isTrue();
        assertThat(counter.getId()).isSameAs(id);
    }

    @Test
    void distributionTest() {
        MeterId id = mock(MeterId.class);
        NoopDistribution distribution = new NoopDistribution(id);
        distribution.record(10);
        assertThat(distribution.measure().isEmpty()).isTrue();
        assertThat(distribution.getId()).isSameAs(id);
    }

    @Test
    void registryTest() {
        NoopMeterRegistry registry = new NoopMeterRegistry();
        assertThat(registry.counter(mock(MeterId.class))).isInstanceOf(NoopCounter.class);
        assertThat(registry.distribution(mock(MeterId.class))).isInstanceOf(NoopDistribution.class);
    }
}