package io.github.anycollect.readers.jmx.application;

import com.google.common.collect.Lists;
import io.github.anycollect.readers.jmx.query.NoopQuery;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrQueryMatcherTest {
    @Test
    void orTest() {
        QueryMatcher left = new SimpleQueryMatcher("group", "left");
        QueryMatcher right = new SimpleQueryMatcher("group", "right");
        List<QueryMatcher> matchers = Lists.newArrayList(left, right);
        OrQueryMatcher or = new OrQueryMatcher(matchers);
        assertThat(or.matches(new NoopQuery("group", "left"))).isTrue();
        assertThat(or.matches(new NoopQuery("group", "right"))).isTrue();
        assertThat(or.matches(new NoopQuery("group", "middle"))).isFalse();
    }
}