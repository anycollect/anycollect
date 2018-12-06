package io.github.anycollect.extensions.common.expression.filters;

import io.github.anycollect.extensions.common.expression.ArgValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class TrimFilterTest {
    @Test
    void trimTest() throws ArgValidationException {
        TrimFilter filter = new TrimFilter();
        String result = filter.filter("  test  ", Collections.emptyList());
        assertThat(result).isEqualTo("test");
    }

    @Test
    void argsTest() {
        TrimFilter filter = new TrimFilter();
        ArgValidationException ex = Assertions.assertThrows(ArgValidationException.class,
                () -> filter.filter("  test  ", Collections.singletonList("arg")));
        assertThat(ex).hasMessageContaining("no arguments");
    }
}