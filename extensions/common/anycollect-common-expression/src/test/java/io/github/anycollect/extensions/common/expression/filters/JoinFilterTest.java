package io.github.anycollect.extensions.common.expression.filters;

import io.github.anycollect.extensions.common.expression.ArgValidationException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class JoinFilterTest {
    @Test
    void varargsTest() throws ArgValidationException {
        JoinFilter filter = new JoinFilter();
        String result = filter.filter("left", Arrays.asList("-middle-", "right"));
        assertThat(result).isEqualTo("left-middle-right");
    }
}