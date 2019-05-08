package io.github.anycollect.extensions.common.expression;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ArgsTest {
    @Test
    void nullsAreForbidden() {
        Args args = MapArgs.builder()
                .add("key", "1")
                .build();
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> args.get("wrong"));
        assertThat(ex).hasMessageContaining("wrong");
        assertThat(args.get("key")).isEqualTo("1");
        Assertions.assertThrows(NullPointerException.class, () -> args.get(null));
    }
}