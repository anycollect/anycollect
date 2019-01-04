package io.github.anycollect.metric;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("Metric ID tests")
class MetricIdTest {
    private MetricId.Builder base = MetricId.builder()
            .key("test")
            .unit("tests")
            .type(Type.GAUGE);

    @Test
    @DisplayName("test getting required tags")
    void baseTest() {
        MetricId id = base.build();
        assertThat(id.getKey()).isEqualTo("test");
        assertThat(id.getUnit()).isEqualTo("tests");
        assertThat(id.getType()).isSameAs(Type.GAUGE);
    }

    @Test
    void metricKeyIsRequired() {
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> MetricId.builder().unit("tests").type(Type.GAUGE).build());
        assertThat(ex).hasMessageContaining(MetricId.METRIC_KEY_TAG);
    }

    @Test
    @DisplayName("IDs with the same tags must be equal")
    void idsWithTheSameTagsMustBeEqual() {
        MetricId id1 = base.tag("key1", "value1").build();
        MetricId id2 = base.tag("key1", "value1").build();
        assertThat(id1).isEqualTo(id2);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }

    @Test
    @DisplayName("IDs with different tag values must be not equal")
    void idsWithDifferentTagValueMustBeNotEqual() {
        MetricId id1 = base.tag("key1", "value1").build();
        MetricId id2 = base.tag("key1", "value2").build();
        assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    @DisplayName("IDs with different tags must be not equal")
    void idsWithDifferentTagsMustBeNotEqual() {
        MetricId id1 = base.tag("key1", "value1").build();
        MetricId id2 = base.tag("key2", "value1").build();
        assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    @DisplayName("IDs with the same tags and different meta tags must be equal")
    void idsWithTheSameTagsAndDifferentMetaTagsMustBeEqual() {
        MetricId id1 = base.tag("key1", "value2").meta("metaKey1", "metaValue1").build();
        MetricId id2 = base.tag("key1", "value2").meta("metaKey2", "metaValue2").build();
        assertThat(id1).isEqualTo(id2);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }

    @Test
    @DisplayName("getting tag value that is not present in id must lead to exception")
    void gettingValueOfNotExistingTagIsIllegal() {
        MetricId id = base.tag("key1", "value1").build();
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> id.getTagValue("key2"));
        assertThat(ex).hasMessageContaining("key2");
        Assertions.assertTrue(id.hasTagKey("key1"));
        Assertions.assertFalse(id.hasTagKey("key2"));
    }

    @Test
    @DisplayName("getting meta tag value that is not present in id must lead to exception")
    void gettingValueOfNotExistingMetaTagIsIllegal() {
        MetricId id = base.meta("key1", "value1").build();
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> id.getMetaTagValue("key2"));
        assertThat(ex).hasMessageContaining("key2");
        Assertions.assertTrue(id.hasMetaTagKey("key1"));
        Assertions.assertFalse(id.hasMetaTagKey("key2"));
    }

    @Test
    @DisplayName("ID must consist of all (and only) tags specified")
    void idMustConsistsOfTagsSpecified() {
        MetricId id = base.tag("key1", "value1")
                .tag("key2", "value2")
                .meta("metaKey1", "metaValue1")
                .build();
        assertThat(id.getTagKeys()).containsExactly("what", "unit", "mtype", "key1", "key2");
        assertThat(id.getMetaTagKeys()).containsExactly("metaKey1");
        assertThat(id.getTagValue("key1")).isEqualTo("value1");
        assertThat(id.getTagValue("key2")).isEqualTo("value2");
        assertThat(id.getMetaTagValue("metaKey1")).isEqualTo("metaValue1");
    }

    @Test
    @DisplayName("fail fast if null was passed to builder")
    void nullsIsForbidden() {
        Assertions.assertThrows(NullPointerException.class, () -> base.tag(null, "value"));
        Assertions.assertThrows(NullPointerException.class, () -> base.tag("key", null));
        Assertions.assertThrows(NullPointerException.class, () -> base.meta(null, "value"));
        Assertions.assertThrows(NullPointerException.class, () -> base.meta("key", null));
    }

    @Test
    void specialTagsTest() {
        MetricId id = base
                .key("http_requests")
                .stat(Stat.max())
                .type(Type.GAUGE)
                .build();
        assertThat(id.getKey()).isEqualTo("http_requests");
        assertThat(id.getStat()).isSameAs(Stat.max());
        assertThat(id.getType()).isSameAs(Type.GAUGE);
        assertThat(id.getTagValue(MetricId.METRIC_KEY_TAG)).isEqualTo("http_requests");
        assertThat(id.getTagValue(MetricId.STAT_TAG)).isEqualTo("max");
        assertThat(id.getTagValue(MetricId.METRIC_TYPE_TAG)).isEqualTo("gauge");
    }

    @Test
    void specialTagsCanBeSetViaGeneralMethod() {
        Assertions.assertDoesNotThrow(() -> MetricId.builder().tag(MetricId.METRIC_KEY_TAG, "http_requests"));
        Assertions.assertDoesNotThrow(() -> MetricId.builder().tag(MetricId.UNIT_TAG, "requests"));
        Assertions.assertDoesNotThrow(() -> MetricId.builder().tag(MetricId.METRIC_TYPE_TAG, "counter"));
        Assertions.assertDoesNotThrow(() -> MetricId.builder().tag(MetricId.STAT_TAG, "min"));
    }

    @Test
    void mustValidateGivenStat() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> base.stat(new Stat() {
            @Override
            public StatType getType() {
                return StatType.MIN;
            }

            @Override
            public String getTagValue() {
                return "min";
            }
        }));
    }
}