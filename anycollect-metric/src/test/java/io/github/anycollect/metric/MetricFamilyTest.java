package io.github.anycollect.metric;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class MetricFamilyTest {
    @Test
    void factoryMethodTest() {
        Measurement measurement = mock(Measurement.class);
        MetricFamily family = MetricFamily.of("key", Tags.of("t1", "v1"), Tags.of("m1", "mv2"), measurement, 1);
        assertThat(family.getTags()).isEqualTo(Tags.of("t1", "v1"));
        assertThat(family.getMeta()).isEqualTo(Tags.of("m1", "mv2"));
        assertThat(family.getMeasurements()).hasSize(1).first().isSameAs(measurement);
    }
}