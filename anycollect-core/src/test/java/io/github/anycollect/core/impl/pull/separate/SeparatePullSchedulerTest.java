package io.github.anycollect.core.impl.pull.separate;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.core.impl.TestQuery;
import io.github.anycollect.core.impl.TestTarget;
import io.github.anycollect.core.impl.pull.availability.CheckingTarget;
import io.github.anycollect.core.impl.scheduler.Scheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class SeparatePullSchedulerTest {
    private SchedulerFactory factory;
    private SeparatePullScheduler puller;
    private Scheduler scheduler;

    @BeforeEach
    void createPullScheduler() {
        factory = mock(SchedulerFactory.class);
        scheduler = mock(Scheduler.class);
        when(factory.create(any(), any())).thenReturn(scheduler);
        Clock clock = mock(Clock.class);
        puller = new SeparatePullScheduler(factory, clock);
    }

    @Nested
    @DisplayName("after schedule two pull jobs for one target")
    class AfterSchedule {
        TestTarget target = mock(TestTarget.class);

        @BeforeEach
        void createPullScheduler() {
            when(target.getId()).thenReturn("id");
            puller.schedulePull(new CheckingTarget<>(target), mock(TestQuery.class), Dispatcher.noop(), 1);
            puller.schedulePull(new CheckingTarget<>(target), mock(TestQuery.class), Dispatcher.noop(), 2);
        }

        @Test
        @DisplayName("scheduler for this target must be created lazily and once")
        void schedulerForEachTargetMustBeCreatedLazilyAndOnce() {
            verify(factory, times(1)).create(eq(target), anyString());
        }

        @Nested
        @DisplayName("after realise")
        class AfterRealise {
            @BeforeEach
            void realiseTarget() {
                puller.release(target);
                puller.release(mock(Target.class));
            }

            @Test
            @DisplayName("pull scheduler must shutdown target's scheduler")
            void mustShutdownTargetScheduler() {
                verify(scheduler, times(1)).shutdown();
            }
        }
    }
}