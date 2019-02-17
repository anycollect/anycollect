package io.github.anycollect.metric;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MeasurementTest {
    @Test
    void counterTest() {
        assertThat(Measurement.counter(10).getType()).isEqualTo(Type.COUNTER);
        assertThat(Measurement.counter(10).getStat()).isEqualTo(Stat.value());
    }

    @Test
    void gaugeTest() {
        assertThat(Measurement.gauge(10).getType()).isEqualTo(Type.GAUGE);
        assertThat(Measurement.gauge(10).getStat()).isEqualTo(Stat.value());
    }

    @Test
    void meanTest() {
        assertThat(Measurement.mean(10).getType()).isEqualTo(Type.GAUGE);
        assertThat(Measurement.mean(10).getStat()).isEqualTo(Stat.mean());
    }

    @Test
    void maxTest() {
        assertThat(Measurement.max(10).getType()).isEqualTo(Type.GAUGE);
        assertThat(Measurement.max(10).getStat()).isEqualTo(Stat.max());
    }

    @Test
    void percentileTest() {
        assertThat(Measurement.percentile(0.99, 100).getType()).isEqualTo(Type.GAUGE);
        assertThat(Measurement.percentile(99, 100).getStat()).isEqualTo(Stat.percentile(99));
    }
}