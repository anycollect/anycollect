package io.github.anycollect.readers.jmx.application;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConcurrencyLevelTest {
    @Test
    void propertiesTest() {
        ConcurrencyLevel concurrencyLevel = new ConcurrencyLevel("high", 2);
        assertThat(concurrencyLevel.getName()).isEqualTo("high");
        assertThat(concurrencyLevel.getMaxNumberOfThreads()).isEqualTo(2);
    }

    @Test
    void maximalNumberOfThreadsMustBeAtLeastOne() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new ConcurrencyLevel("name", 0));
    }

    @Test
    void nameMustNotBeNull() {
        NullPointerException npe = Assertions.assertThrows(NullPointerException.class, () -> new ConcurrencyLevel(null, 1));
        assertThat(npe)
                .hasMessageContaining("name")
                .hasMessageContaining("must not be null");
    }
}