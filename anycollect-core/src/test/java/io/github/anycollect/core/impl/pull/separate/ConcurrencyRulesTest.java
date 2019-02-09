package io.github.anycollect.core.impl.pull.separate;

import io.github.anycollect.core.api.target.Target;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConcurrencyRulesTest {
    @Nested
    @DisplayName("when several rules is accepted")
    class WhenSeveralRulesIsAccepted {
        private ConcurrencyRules rules;
        private Target<?> target;

        @BeforeEach
        void setUp() {
            target = mock(Target.class);
            ConcurrencyRule rule1 = mock(ConcurrencyRule.class);
            ConcurrencyRule rule2 = mock(ConcurrencyRule.class);
            when(rule1.getPoolSize(eq(target), anyInt())).thenReturn(3);
            when(rule2.getPoolSize(eq(target), anyInt())).thenReturn(5);
            rules = ConcurrencyRules.builder()
                    .withRule(rule1)
                    .withRule(rule2)
                    .build();
        }

        @Test
        @DisplayName("the first one must be used")
        void theFirstOneMustBeUsed() {
            assertThat(rules.getPoolSize(target, -1)).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("when no appropriate rules found")
    class WhenNoAppropriateRulesFound {
        private ConcurrencyRules rules;

        @BeforeEach
        void setUp() {
            ConcurrencyRule rule = mock(ConcurrencyRule.class);
            when(rule.getPoolSize(any(), anyInt())).thenAnswer((Answer<Integer>) invocation -> (int) invocation.getArgument(1));
            rules = ConcurrencyRules.builder()
                    .withRule(rule)
                    .build();
        }

        @Test
        @DisplayName("must return fallback")
        void mustReturnFallback() {
            Target target = mock(Target.class);
            assertThat(rules.getPoolSize(target, -1)).isEqualTo(-1);
            assertThat(rules.getPoolSize(target, 10)).isEqualTo(10);
        }
    }
}