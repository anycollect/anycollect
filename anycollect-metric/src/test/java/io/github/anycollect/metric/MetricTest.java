package io.github.anycollect.metric;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MetricTest {
    @Test
    void idMustNotBeNull() {
        NullPointerException npe = Assertions.assertThrows(NullPointerException.class,
                () -> new Metric(null, 1, 1));
        assertThat(npe).hasMessage("id must not be null");
    }

    @Test
    void baseTest() {
        Metric metric = new Metric(MetricId.builder().tag("key", "metric").build(), 1, 2);
        assertThat(metric.getTimestamp()).isEqualTo(2);
        assertThat(metric.getValue()).isEqualTo(1);
    }
}