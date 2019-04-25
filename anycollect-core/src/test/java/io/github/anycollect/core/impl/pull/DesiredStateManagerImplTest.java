package io.github.anycollect.core.impl.pull;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.api.internal.ImmutableState;
import io.github.anycollect.core.api.internal.State;
import io.github.anycollect.core.impl.NoopCancelation;
import io.github.anycollect.core.impl.TestQuery;
import io.github.anycollect.core.impl.TestTarget;
import io.github.anycollect.core.impl.pull.availability.CheckingTarget;
import io.github.anycollect.core.impl.pull.availability.HealthChecker;
import io.github.anycollect.core.impl.scheduler.Cancellation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class DesiredStateManagerImplTest {
    private PullScheduler pullScheduler = mock(PullScheduler.class);
    private Dispatcher dispatcher = Dispatcher.noop();
    private DesiredStateManagerImpl<TestTarget, TestQuery> manager;

    @BeforeEach
    void setUp() {
        when(pullScheduler.schedulePull(any(), any(), any(), anyInt())).thenReturn(new NoopCancelation());
    }

    @Test
    @DisplayName("is instantiated")
    void isInstantiatedWithNew() {
        new DesiredStateManagerImpl<>(pullScheduler, dispatcher, HealthChecker.noop());
    }

    @Nested
    @DisplayName("after target with two queries added")
    class AfterTargetWithTwoQueriesAdded {
        private TestTarget target1 = mock(TestTarget.class);
        private CheckingTarget<TestTarget> checkingTarget1;
        private TestQuery query11 = new TestQuery("id11");
        private TestQuery query12 = new TestQuery("id12");
        private State<TestTarget, TestQuery> state;
        private Cancellation query11Cancellation = mock(Cancellation.class);
        private Cancellation query12Cancellation = mock(Cancellation.class);

        @BeforeEach
        void createManager() {
            Clock clock = mock(Clock.class);
            when(clock.wallTime()).thenReturn(0L);
            manager = new DesiredStateManagerImpl<>(pullScheduler, dispatcher, clock);
            state = ImmutableState.<TestTarget, TestQuery>builder()
                    .put(target1, query11, 1)
                    .put(target1, query12, 2)
                    .build();
            checkingTarget1 = new CheckingTarget<>(target1, 0);
            when(pullScheduler.schedulePull(eq(checkingTarget1), eq(query11), any(), anyInt())).thenReturn(query11Cancellation);
            when(pullScheduler.schedulePull(eq(checkingTarget1), eq(query12), any(), anyInt())).thenReturn(query12Cancellation);
            manager.update(state);
        }

        @Test
        @DisplayName("pull jobs have been scheduled")
        void pullJobsHaveBeenScheduled() {
            verify(pullScheduler, times(1)).schedulePull(eq(checkingTarget1), same(query11), same(dispatcher), eq(1));
            verify(pullScheduler, times(1)).schedulePull(eq(checkingTarget1), same(query12), same(dispatcher), eq(2));
        }

        @Nested
        @DisplayName("when remove target from new state")
        class WhenRemoveTargetFromNewState {
            private TestTarget target2 = mock(TestTarget.class);
            private TestQuery query21 = new TestQuery("id21");
            private TestQuery query22 = new TestQuery("id22");
            private State<TestTarget, TestQuery> state;

            @BeforeEach
            void updateState() {
                state = ImmutableState.<TestTarget, TestQuery>builder()
                        .put(target2, query21, 3)
                        .put(target2, query22, 4)
                        .build();
                manager.update(state);
            }

            @Test
            @DisplayName("obsolete target must be realised")
            void obsoleteTargetMustBeRealised() {
                verify(pullScheduler, times(1)).release(target1);
            }

            @Test
            @DisplayName("new target must be scheduled")
            void newOneMustBeScheduled() {
                verify(pullScheduler, times(1)).schedulePull(eq(new CheckingTarget<>(target2, 0)), same(query21), same(dispatcher), eq(3));
                verify(pullScheduler, times(1)).schedulePull(eq(new CheckingTarget<>(target2, 0)), same(query22), same(dispatcher), eq(4));
            }
        }

        @Nested
        @DisplayName("when change queries to existing target")
        class WhenChangeQueriesToExistingTarget {
            private TestQuery query13 = new TestQuery("id13");
            private TestQuery query14 = new TestQuery("id14");
            private State<TestTarget, TestQuery> state;

            @BeforeEach
            void updateState() {
                state = ImmutableState.<TestTarget, TestQuery>builder()
                        .put(target1, query13, 5)
                        .put(target1, query14, 6)
                        .build();
                manager.update(state);
            }

            @Test
            @DisplayName("this target must not be realised")
            void targetMustNotBeRealised() {
                verify(pullScheduler, never()).release(target1);
            }

            @Test
            @DisplayName("obsolete queries must be cancelled")
            void obsoleteQueriesMustBeCancelled() {
                verify(query11Cancellation, times(1)).cancel();
                verify(query12Cancellation, times(1)).cancel();
            }

            @Test
            @DisplayName("additional queries must be added")
            void additionalQueriesMustBeAdded() {
                verify(pullScheduler, times(1)).schedulePull(eq(checkingTarget1), same(query13), same(dispatcher), eq(5));
                verify(pullScheduler, times(1)).schedulePull(eq(checkingTarget1), same(query14), same(dispatcher), eq(6));
            }
        }

        @Nested
        @DisplayName("when add queries to existing target")
        class WhenAddQueriesToExistingTarget {
            private TestQuery query15 = new TestQuery("id15");
            private TestQuery query16 = new TestQuery("id16");
            private State<TestTarget, TestQuery> state;

            @BeforeEach
            void updateState() {
                state = ImmutableState.<TestTarget, TestQuery>builder()
                        .put(target1, query11, 1)
                        .put(target1, query12, 2)
                        .put(target1, query15, 7)
                        .put(target1, query16, 8)
                        .build();
                manager.update(state);
            }

            @Test
            @DisplayName("this target must not be realised")
            void targetMustNotBeRealised() {
                verify(pullScheduler, never()).release(target1);
            }

            @Test
            @DisplayName("previous queries must not be rescheduled")
            void previousQueriesMustNotBeRescheduled() {
                verify(query11Cancellation, times(0)).cancel();
                verify(query12Cancellation, times(0)).cancel();
                verify(pullScheduler, times(1)).schedulePull(eq(checkingTarget1), same(query11), same(dispatcher), eq(1));
                verify(pullScheduler, times(1)).schedulePull(eq(checkingTarget1), same(query12), same(dispatcher), eq(2));
            }

            @Test
            @DisplayName("additional queries must be added")
            void additionalQueriesMustBeAdded() {
                verify(pullScheduler, times(1)).schedulePull(eq(checkingTarget1), same(query15), same(dispatcher), eq(7));
                verify(pullScheduler, times(1)).schedulePull(eq(checkingTarget1), same(query16), same(dispatcher), eq(8));
            }
        }

        @Nested
        @DisplayName("when change period for existing queries")
        class WhenChangePeriodForExistingQueries {
            private State<TestTarget, TestQuery> state;

            @BeforeEach
            void updateState() {
                state = ImmutableState.<TestTarget, TestQuery>builder()
                        .put(target1, query11, 9)
                        .put(target1, query12, 10)
                        .build();
                manager.update(state);
            }

            @Test
            @DisplayName("this target must not be realised")
            void targetMustNotBeRealised() {
                verify(pullScheduler, never()).release(target1);
            }

            @Test
            @DisplayName("previous queries must be cancelled")
            void previousQueriesMustBeCancelled() {
                verify(query11Cancellation, times(1)).cancel();
                verify(query12Cancellation, times(1)).cancel();
            }

            @Test
            @DisplayName("queries with new period must be added")
            void queriesWithNewPeriodMustBeAdded() {
                verify(pullScheduler, times(1)).schedulePull(eq(checkingTarget1), same(query11), same(dispatcher), eq(9));
                verify(pullScheduler, times(1)).schedulePull(eq(checkingTarget1), same(query12), same(dispatcher), eq(10));
            }
        }

        @Nested
        @DisplayName("when destroy")
        class WhenDestroy {
            @BeforeEach
            void destroyManager() {
                manager.destroy();
            }

            @Test
            @DisplayName("all queries must be cancelled")
            void allQueriesMustBeCancelled() {
                verify(query11Cancellation, times(1)).cancel();
                verify(query12Cancellation, times(1)).cancel();
            }

            @Test
            @DisplayName("all targets must be realised")
            void allTargetsMustBeRealised() {
                verify(pullScheduler, times(1)).release(target1);
            }
        }
    }

//    private static void verify() {
//
//    }
}