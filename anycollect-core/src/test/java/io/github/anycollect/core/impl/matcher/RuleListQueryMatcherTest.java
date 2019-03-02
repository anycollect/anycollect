package io.github.anycollect.core.impl.matcher;

import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RuleListQueryMatcherTest {
    private Target target = mock(Target.class);
    private Query query = mock(Query.class);

    @Test
    void smallestPeriodMustBeChosen() {
        MatchRule rule1 = mock(MatchRule.class);
        when(rule1.match(target, query)).thenReturn(true);
        when(rule1.getPeriod()).thenReturn(40);

        MatchRule rule2 = mock(MatchRule.class);
        when(rule2.match(target, query)).thenReturn(true);
        when(rule2.getPeriod()).thenReturn(30);

        assertThat(new RuleListQueryMatcher(Arrays.asList(rule1, rule2))
                .getPeriodInSeconds(target, query, -1)).isEqualTo(30);
        assertThat(new RuleListQueryMatcher(Arrays.asList(rule2, rule1))
                .getPeriodInSeconds(target, query, -1)).isEqualTo(30);
    }

    @Test
    void periodMustNotBeOverwrittenIfNewRuleDoesNotProvidePositivePeriod() {
        MatchRule rule1 = mock(MatchRule.class);
        when(rule1.match(target, query)).thenReturn(true);
        when(rule1.getPeriod()).thenReturn(40);

        MatchRule rule2 = mock(MatchRule.class);
        when(rule2.match(target, query)).thenReturn(true);
        when(rule2.getPeriod()).thenReturn(-1);

        assertThat(new RuleListQueryMatcher(Arrays.asList(rule1, rule2))
                .getPeriodInSeconds(target, query, -1)).isEqualTo(40);
        assertThat(new RuleListQueryMatcher(Arrays.asList(rule2, rule1))
                .getPeriodInSeconds(target, query, -1)).isEqualTo(40);
    }

    @Test
    void mustReturnMinusOneIfNoRuleFound() {
        MatchRule rule1 = mock(MatchRule.class);
        when(rule1.match(target, query)).thenReturn(false);
        assertThat(new RuleListQueryMatcher(Collections.singletonList(rule1))
                .getPeriodInSeconds(target, query, 30)).isEqualTo(-1);
    }
}