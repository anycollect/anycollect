package io.github.anycollect.core.impl.pull;

import io.github.anycollect.core.api.internal.DesiredStateProvider;
import io.github.anycollect.core.api.internal.State;
import io.github.anycollect.core.impl.TestQuery;
import io.github.anycollect.core.impl.TestTarget;
import io.github.anycollect.core.impl.pull.availability.HealthChecker;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class DesiredStateUpdateJobTest {
    @Test
    @SuppressWarnings("unchecked")
    void mustUpdateManagerStateFromProvider() {
        DesiredStateProvider<TestTarget, TestQuery> provider = mock(DesiredStateProvider.class);
        DesiredStateManager<TestTarget, TestQuery> manager = mock(DesiredStateManager.class);
        DesiredStateUpdateJob<TestTarget, TestQuery> job =
                new DesiredStateUpdateJob<>(provider, manager, mock(HealthChecker.class));
        State<TestTarget, TestQuery> state = mock(State.class);
        when(provider.current()).thenReturn(state);
        job.run();
        verify(manager, times(1)).update(state);
    }
}