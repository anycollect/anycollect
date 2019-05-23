package io.github.anycollect.metric;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StatTest {
    @Test
    void parseStringTest() {
        assertThat(Stat.parse("min")).isSameAs(Stat.min());
        assertThat(Stat.parse("max")).isSameAs(Stat.max());
        assertThat(Stat.parse("mean")).isSameAs(Stat.mean());
        assertThat(Stat.parse("std")).isSameAs(Stat.std());
        assertThat(Stat.parse("max_99")).isEqualTo(Percentile.of(Stat.max(), 99));
        assertThat(Stat.parse("le_Infinity")).isInstanceOf(LeBucket.class)
                .extracting(stat -> ((LeBucket) stat).getMax())
                .isEqualTo(Double.POSITIVE_INFINITY);
        assertThat(Stat.parse("le_100")).isInstanceOf(LeBucket.class)
                .extracting(stat -> ((LeBucket) stat).getMax())
                .isEqualTo(100.0);
        assertThat(Stat.parse("le_0.05")).isInstanceOf(LeBucket.class)
                .extracting(stat -> ((LeBucket) stat).getMax())
                .isEqualTo(0.05);
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> Stat.parse("undefined"));
        assertThat(ex).hasMessageContaining("undefined");
    }

    @Test
    void typeTest() {
        assertThat(Stat.min().getType()).isSameAs(StatType.MIN);
        assertThat(Stat.max().getType()).isSameAs(StatType.MAX);
        assertThat(Stat.mean().getType()).isSameAs(StatType.MEAN);
        assertThat(Stat.std().getType()).isSameAs(StatType.STD);
        assertThat(Stat.percentile(95).getType()).isSameAs(StatType.PERCENTILE);
        assertThat(Stat.leInf().getType()).isSameAs(StatType.LE_BUCKET);
        assertThat(Stat.value().getType()).isSameAs(StatType.UNKNOWN);
    }

    @Test
    void tagValueTest() {
        assertThat(Stat.min().getTagValue()).isEqualTo("min").isEqualTo(Stat.min().toString());
        assertThat(Stat.max().getTagValue()).isEqualTo("max").isEqualTo(Stat.max().toString());
        assertThat(Stat.mean().getTagValue()).isEqualTo("mean").isEqualTo(Stat.mean().toString());
        assertThat(Stat.std().getTagValue()).isEqualTo("std").isEqualTo(Stat.std().toString());
        assertThat(Stat.percentile(99).getTagValue()).isEqualTo("max_99").isEqualTo(Stat.percentile(99).toString());
        assertThat(Stat.percentile(999).getTagValue()).isEqualTo("max_999").isEqualTo(Stat.percentile(999).toString());
        assertThat(Stat.le(0.4).getTagValue()).isEqualTo("le_0.4").isEqualTo(Stat.le(0.4).toString());
        assertThat(Stat.le(120).getTagValue()).isEqualTo("le_120").isEqualTo(Stat.le(120).toString());
        assertThat(Stat.leInf().getTagValue()).isEqualTo("le_Infinity").isEqualTo(Stat.leInf().toString());
        assertThat(Stat.value().getTagValue()).isEqualTo("value").isEqualTo(Stat.value().toString());
    }

    @Test
    void validationTest() {
        assertThat(Stat.isValid(Stat.min())).isTrue();
        assertThat(Stat.isValid(Stat.max())).isTrue();
        assertThat(Stat.isValid(Stat.mean())).isTrue();
        assertThat(Stat.isValid(Stat.std())).isTrue();
        assertThat(Stat.isValid(Percentile.of(Stat.max(), 75))).isTrue();
        assertThat(Stat.isValid(LeBucket.inf())).isTrue();
        assertThat(Stat.isValid(LeBucket.of(100))).isTrue();
        assertThat(Stat.isValid(LeBucket.of(0.6))).isTrue();
        assertThat(Stat.isValid(new Stat() {
            @Override
            public StatType getType() {
                return StatType.MIN;
            }

            @Override
            public String getTagValue() {
                return "custom_min";
            }
        })).isFalse();
    }
}