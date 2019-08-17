package io.github.anycollect.metric;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MetricTest {
    @Test
    void toStringTest() {
        Metric metric = Metric.builder()
                .key("key")
                .tags(Tags.of("key1", "value1"))
                .meta("key2", "value2")
                .min("ms");
        Sample sample = metric.sample(2.0, 1);
        assertThat(sample.toString()).isEqualTo("key;key1=value1;min[a](ms) 2.0 1");
    }

    @Test
    void toStringWithEmptyTagsTest() {
        Metric metric = Metric.builder()
                .key("key")
                .empty()
                .empty()
                .min("ms");
        Sample sample = metric.sample(2.0, 1);
        assertThat(sample.toString()).isEqualTo("key;min[a](ms) 2.0 1");
    }
}