package io.github.anycollect.core.impl.pull;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.core.impl.TestQuery;
import io.github.anycollect.core.impl.TestTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class PullJobTest {
    private TestTarget target = mock(TestTarget.class);
    private TestQuery query = new TestQuery("id");
    private Dispatcher dispatcher = mock(Dispatcher.class);
    private PullJob<TestTarget, TestQuery> job;

    @BeforeEach
    void setUp() {
        when(target.getId()).thenReturn("id");
        job = new PullJob<>(target, query, dispatcher);
    }

    @Test
    void targetHasBeenExecuted() throws ConnectionException, QueryException {
        job.run();
        verify(target, times(1)).execute(query, dispatcher);
    }
}