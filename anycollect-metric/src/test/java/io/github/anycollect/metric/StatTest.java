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
        assertThat(Stat.parse("99_NUM")).isEqualTo(new Percentile(99));
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
    }

    @Test
    void tagValueTest() {
        assertThat(Stat.min().getTagValue()).isEqualTo("min");
        assertThat(Stat.max().getTagValue()).isEqualTo("max");
        assertThat(Stat.mean().getTagValue()).isEqualTo("mean");
        assertThat(Stat.std().getTagValue()).isEqualTo("std");
        assertThat(Stat.percentile(99).getTagValue()).isEqualTo("99_NUM");
        assertThat(Stat.percentile(999).getTagValue()).isEqualTo("999_NUM");
    }

    @Test
    void validationTest() {
        assertThat(Stat.isValid(Stat.min())).isTrue();
        assertThat(Stat.isValid(Stat.max())).isTrue();
        assertThat(Stat.isValid(Stat.mean())).isTrue();
        assertThat(Stat.isValid(Stat.std())).isTrue();
        assertThat(Stat.isValid(new Percentile(75))).isTrue();
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