package io.github.anycollect.core.impl.pull.separate;

import io.github.anycollect.core.api.target.Target;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RegExpConcurrencyRuleTest {
    @Nested
    @DisplayName("when match")
    class WhenMatch {
        private RegExpConcurrencyRule rule;

        @BeforeEach
        void setUp() {
            rule = new RegExpConcurrencyRule("app[0-9]+", 3);
        }

        @Test
        @DisplayName("must return configured pool size")
        void mustReturnConfiguredPoolSize() {
            Target target = mock(Target.class);
            when(target.getLabel()).thenReturn("app1");
            assertThat(rule.getPoolSize(target, -1)).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("when not match")
    class WhenNotMatch {
        private RegExpConcurrencyRule rule;

        @BeforeEach
        void setUp() {
            rule = new RegExpConcurrencyRule("app", 3);
        }

        @Test
        @DisplayName("must return fallback")
        void mustReturnFallback() {
            Target target = mock(Target.class);
            when(target.getLabel()).thenReturn("app1");
            assertThat(rule.getPoolSize(target, -1)).isEqualTo(-1);
            assertThat(rule.getPoolSize(target, 10)).isEqualTo(10);
        }
    }
}