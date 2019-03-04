package io.github.anycollect.metric;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MetricTest {
    @Test
    void idMustNotBeNull() {
        NullPointerException npe = Assertions.assertThrows(NullPointerException.class,
                () -> Point.of(null, 1, 1));
        assertThat(npe).hasMessage("id must not be null");
    }

    @Test
    void baseTest() {
        Point point = Point.of(PointId.key("metric").stat(Stat.value()).unit("metrics").type(Type.GAUGE).build(), 1, 2);
        assertThat(point.getTimestamp()).isEqualTo(2);
        assertThat(point.getValue()).isEqualTo(1);
    }
}