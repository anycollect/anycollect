package io.github.anycollect.metric;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class ImmutableMetricFamilyTest {
    @Test
    void toStringTest() {
        ImmutableMetric family = new ImmutableMetric(
                "key",
                1,
                Collections.singletonList(new ImmutableMeasurement(
                        Stat.min(),
                        Type.GAUGE,
                        "ms",
                        2.0
                )),
                Tags.of("key1", "value1"),
                Tags.of("key2", "value2")
        );
        assertThat(family.toString()).isEqualTo("key;key1=value1;min[g]=2.0(ms)");
    }

    @Test
    void toStringWithEmptyTagsTest() {
        ImmutableMetric family = new ImmutableMetric(
                "key",
                1,
                Collections.singletonList(new ImmutableMeasurement(
                        Stat.min(),
                        Type.GAUGE,
                        "ms",
                        2.0
                )),
                Tags.empty(),
                Tags.empty()
        );
        assertThat(family.toString()).isEqualTo("key;min[g]=2.0(ms)");
    }
}