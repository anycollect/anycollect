package io.github.anycollect.extensions.common.expression.filters;

import io.github.anycollect.extensions.common.expression.ArgValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class MatchReplaceFilterTest {
    private MatchReplaceFilter filter = new MatchReplaceFilter();
    @Test
    void replaceTest() throws ArgValidationException {
        String result = filter.filter("localhost:80", Arrays.asList("([a-z]+):([0-9]+)", "host.$1.port.$2"));
        assertThat(result).isEqualTo("host.localhost.port.80");
    }

    @Test
    @DisplayName("MatchReplace filter requires two arguments")
    void argsTest() {
        ArgValidationException ex1 = Assertions.assertThrows(ArgValidationException.class,
                () -> filter.filter("localhost:80", Collections.singletonList("([a-z]+):([0-9]+)")));
        assertThat(ex1).hasMessageContaining("two");
        ArgValidationException ex3 = Assertions.assertThrows(ArgValidationException.class,
                () -> filter.filter("localhost:80", Arrays.asList("([a-z]+):([0-9]+)", "host.$1.port.$2", "fake")));
        assertThat(ex3).hasMessageContaining("two");
    }
}