package io.github.anycollect.core.impl.pull.separate;

import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.core.impl.scheduler.SchedulerImpl;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SchedulerFactoryImplTest {
    private ConcurrencyRule rule;
    private SchedulerFactoryImpl factory;
    private Target target = mock(Target.class);

    @BeforeEach
    void setUp() {
        rule = mock(ConcurrencyRule.class);
        factory = new SchedulerFactoryImpl(rule, 2, new SimpleMeterRegistry());
        when(target.getLabel()).thenReturn("app");
    }

    @Test
    @DisplayName("when rule found then must return rule pool size")
    void whenRuleFoundThenMustReturnRulePoolSize() {
        when(rule.getPoolSize(eq(target), anyInt())).thenReturn(1);
        SchedulerImpl scheduler = (SchedulerImpl) factory.create(target);
        assertThat(scheduler.getPoolSize()).isEqualTo(1);
    }

    @Test
    @DisplayName("when no rules found then must return default pool size")
    void whenNoRulesFoundThenMustReturnDefaultPoolSize() {
        when(rule.getPoolSize(any(), anyInt())).thenAnswer(invocation -> invocation.getArgument(1));
        SchedulerImpl scheduler = (SchedulerImpl) factory.create(target);
        assertThat(scheduler.getPoolSize()).isEqualTo(2);
    }
}