package io.github.anycollect.metric;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// TODO separate and make more detailed checks after test assertj extension is created
class MeterIdTest {
    @Test
    void defaultMethods() {
        Tags tags = Tags.builder()
                .tag(CommonTags.METRIC_KEY.getKey(), "time")
                .tag(CommonTags.UNIT.getKey(), "ms")
                .build();
        Tags meta = Tags.builder()
                .tag("meta", "data")
                .build();
        MeterId id = new MeterId() {
            @Override
            public Tags getTags() {
                return tags;
            }

            @Override
            public Tags getMetaTags() {
                return meta;
            }
        };

        assertThat(id.getKey()).isEqualTo("time");
        assertThat(id.getUnit()).isEqualTo("ms");
        assertThat(id.counter().getUnit()).isEqualTo("ms");
        assertThat(id.counter("ns").getUnit()).isEqualTo("ns");
        assertThat(id.percentile(99, "ns").getStat())
                .isInstanceOf(Percentile.class).isEqualTo(Stat.percentile(99));
        assertThat(id.percentile(95 ).getUnit()).isEqualTo("ms");
        assertThat(id.percentile(95, "ns").getUnit()).isEqualTo("ns");
        assertThat(id.percentile(0.95).getUnit()).isEqualTo("ms");
        assertThat(id.percentile(0.99, "ns").getUnit()).isEqualTo("ns");
        assertThat(id.max().getStat()).isEqualTo(Stat.max());
        assertThat(id.mean().getStat()).isEqualTo(Stat.mean());
        assertThat(id.value().getStat()).isEqualTo(Stat.value());
    }
}