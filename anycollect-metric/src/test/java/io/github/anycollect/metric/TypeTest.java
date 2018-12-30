package io.github.anycollect.metric;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TypeTest {
    @Test
    void tagValueTest() {
        assertThat(Type.RATE.getTagValue()).isEqualTo("rate");
        assertThat(Type.COUNT.getTagValue()).isEqualTo("count");
        assertThat(Type.GAUGE.getTagValue()).isEqualTo("gauge");
        assertThat(Type.COUNTER.getTagValue()).isEqualTo("counter");
        assertThat(Type.TIMESTAMP.getTagValue()).isEqualTo("timestamp");
    }

    @Test
    void failIfTypeIsUndefined() {
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> Type.parse("undefined"));
        assertThat(ex).hasMessageContaining("undefined");
    }
}