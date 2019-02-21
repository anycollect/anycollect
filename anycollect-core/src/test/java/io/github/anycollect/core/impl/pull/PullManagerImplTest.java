package io.github.anycollect.core.impl.pull;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.internal.DesiredStateProvider;
import io.github.anycollect.core.api.internal.ImmutableState;
import io.github.anycollect.core.api.internal.State;
import io.github.anycollect.core.impl.JitScheduler;
import io.github.anycollect.core.impl.TestQuery;
import io.github.anycollect.core.impl.TestTarget;
import io.github.anycollect.core.impl.scheduler.Scheduler;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// TODO remove
class PullManagerImplTest {
    @Test
    void scheduleInitialState() {
        PullScheduler scheduler = mock(PullScheduler.class);
        Scheduler updater = new JitScheduler();
        PullManagerImpl manager = new PullManagerImpl(scheduler, updater, mock(Scheduler.class),1, 10);
        @SuppressWarnings("unchecked")
        DesiredStateProvider<TestTarget, TestQuery> provider = mock(DesiredStateProvider.class);
        Dispatcher dispatcher = mock(Dispatcher.class);
        @SuppressWarnings("unchecked")
        TestTarget target1 = mock(TestTarget.class);
        TestQuery query11 = new TestQuery("id1");
        TestQuery query12 = new TestQuery("id2");
        State<TestTarget, TestQuery> state = ImmutableState.<TestTarget, TestQuery>builder()
                .put(target1, query11, 1)
                .put(target1, query12, 2)
                .build();
        when(provider.current()).thenReturn(state);
        manager.start(provider, dispatcher, mock(TestQuery.class));
        verify(scheduler, times(1)).schedulePull(eq(target1), eq(query11), any(), eq(1));
        verify(scheduler, times(1)).schedulePull(eq(target1), eq(query12), any(), eq(2));
    }
}