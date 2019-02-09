package io.github.anycollect.core.impl.pull;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.core.impl.TestQuery;
import io.github.anycollect.core.impl.TestTarget;
import io.github.anycollect.metric.Metric;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CallbackToDispatcherAdapterTest {
    private Dispatcher dispatcher;
    private CallbackToDispatcherAdapter<TestTarget, TestQuery> adapter;

    @BeforeEach
    void createAdapter() {
        dispatcher = mock(Dispatcher.class);
        adapter = new CallbackToDispatcherAdapter<>(dispatcher);
    }

    @Nested
    @DisplayName("when success")
    class WhenSuccess {
        private List<Metric> metrics = new ArrayList<>();

        @BeforeEach
        void success() {
            TestTarget target = mock(TestTarget.class);
            TestQuery query = mock(TestQuery.class);
            adapter.call(Result.success(target, query, metrics, 10));
        }

        @Test
        @DisplayName("must forward metrics to dispatcher")
        void mustForwardMetricsToDispatcher() {
            verify(dispatcher, times(1)).dispatch(metrics);
        }
    }

    @Nested
    @DisplayName("when fail")
    class WhenFail {
        @BeforeEach
        void success() {
            TestTarget target = mock(TestTarget.class);
            TestQuery query = mock(TestQuery.class);
            adapter.call(Result.fail(target, query, new QueryException(), 10));
        }

        @Test
        @DisplayName("must not invoke dispatcher")
        @SuppressWarnings("unchecked")
        void mustNotInvokeDispatcher() {
            verify(dispatcher, times(0)).dispatch(any(List.class));
        }
    }
}