package io.github.anycollect.metric;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PercentileTest {
    @Test
    void percentileValueMustBePositive() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Percentile(Stat.max(), 0));
    }
}