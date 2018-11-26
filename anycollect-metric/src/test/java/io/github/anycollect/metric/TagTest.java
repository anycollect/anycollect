package io.github.anycollect.metric;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tag test")
class TagTest {
    @Test
    @DisplayName("only tags with the same keys and values must be equal to each other")
    void equalityTest() {
        assertThat(Tag.of("key1", "value1")).isEqualTo(Tag.of("key1", "value1"));
        assertThat(Tag.of("key1", "value1")).isNotEqualTo(Tag.of("key1", "value2"));
        assertThat(Tag.of("key1", "value1")).isNotEqualTo(Tag.of("key2", "value1"));
    }

    @Test
    @DisplayName("there is no way to create tag with null key or value")
    void nullsIsForbidden() {
        NullPointerException ex1 = Assertions.assertThrows(NullPointerException.class, () -> Tag.of(null, "value1"));
        assertThat(ex1).hasMessageContaining("key");
        NullPointerException ex2 = Assertions.assertThrows(NullPointerException.class, () -> Tag.of("key1", null));
        assertThat(ex2).hasMessageContaining("value");
    }
}