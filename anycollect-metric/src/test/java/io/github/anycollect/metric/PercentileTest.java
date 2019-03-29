package io.github.anycollect.metric;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PercentileTest {
    @Test
    void percentileValueMustBePositive() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Percentile.of(Stat.max(), 0));
    }

    @Test
    void doublePercentileMustBeConvertedToInteger() {
        assertThat(Stat.percentile(0.5).getTagValue()).isEqualTo("max_50");
        assertThat(Stat.percentile(0.75).getTagValue()).isEqualTo("max_75");
        assertThat(Stat.percentile(0.95).getTagValue()).isEqualTo("max_95");
        assertThat(Stat.percentile(0.99).getTagValue()).isEqualTo("max_99");
        assertThat(Stat.percentile(0.999).getTagValue()).isEqualTo("max_999");
    }

    @Test
    void differentStatsCanBeUsedForPercentile() {
        assertThat(Stat.percentile(Stat.MEAN, 0.99).getStat()).isEqualTo(Stat.mean());
    }
}