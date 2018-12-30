package io.github.anycollect.readers.jmx.application;

import io.github.anycollect.readers.jmx.query.NoopQuery;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QueryMatcherTest {
    @Test
    void mustMatchByGroupAndLabel() {
        SimpleQueryMatcher matcher = new SimpleQueryMatcher("group", "label");
        assertThat(matcher.matches(new NoopQuery("group", "label"))).isTrue();
        assertThat(matcher.matches(new NoopQuery("group", "wrong"))).isFalse();
        assertThat(matcher.matches(new NoopQuery("wrong", "label"))).isFalse();
    }
}