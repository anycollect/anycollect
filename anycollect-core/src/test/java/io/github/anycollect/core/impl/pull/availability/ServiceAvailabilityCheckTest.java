package io.github.anycollect.core.impl.pull.availability;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.internal.Clock;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.impl.TestQuery;
import io.github.anycollect.core.impl.TestTarget;
import io.github.anycollect.core.impl.pull.PullScheduler;
import io.github.anycollect.core.impl.scheduler.Cancellation;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Stat;
import io.github.anycollect.metric.Type;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static io.github.anycollect.assertj.AnyCollectAssertions.assertThat;
import static org.mockito.Mockito.*;

class ServiceAvailabilityCheckTest {
    @Test
    void healthCheckMetricsIsIncrementedCorrectly() throws Exception {
        List<Metric> families = makeCheck(false);
        Metric up = getFamilyByKey(families, "up");
        Metric down = getFamilyByKey(families, "down");
        Metric timeout = getFamilyByKey(families, "timeout");
        assertThat(up).hasMeasurement(Stat.value(), Type.GAUGE, "instances", 1.0);
        assertThat(down).hasMeasurement(Stat.value(), Type.GAUGE, "instances", 1.0);
        assertThat(timeout).hasMeasurement(Stat.value(), Type.GAUGE, "instances", 0.0);
    }

    @Test
    void healthCheckMetricsIsIncrementedCorrectlyWhenTimeout() throws Exception {
        List<Metric> families = makeCheck(true);
        Metric up = getFamilyByKey(families, "up");
        Metric down = getFamilyByKey(families, "down");
        Metric timeout = getFamilyByKey(families, "timeout");
        assertThat(up).hasMeasurement(Stat.value(), Type.GAUGE, "instances", 0.0);
        assertThat(down).hasMeasurement(Stat.value(), Type.GAUGE, "instances", 0.0);
        assertThat(timeout).hasMeasurement(Stat.value(), Type.GAUGE, "instances", 2.0);
    }

    private static List<Metric> makeCheck(boolean timeout) throws Exception {
        Dispatcher dispatcher = mock(Dispatcher.class);
        TestTarget target1 = mock(TestTarget.class);
        when(target1.bind(any())).thenCallRealMethod();
        TestTarget target2 = mock(TestTarget.class);
        when(target2.bind(any())).thenCallRealMethod();
        TestQuery query = mock(TestQuery.class);
        when(target1.execute(query)).thenReturn(Collections.emptyList());
        when(target2.execute(query)).thenThrow(ConnectionException.class);
        PullScheduler scheduler = new MockPullScheduler(timeout);
        ServiceAvailabilityCheck<TestTarget, TestQuery> check = ServiceAvailabilityCheck.<TestTarget, TestQuery>builder()
                .clock(Clock.getDefault())
                .dispatcher(dispatcher)
                .scheduler(scheduler)
                .period(2)
                .timeout(1)
                .service("sample")
                .targets(Lists.list(target1, target2))
                .healthCheck(query)
                .build();
        check.run();
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Metric>> captor = ArgumentCaptor.forClass(List.class);
        verify(dispatcher, times(1)).dispatch(captor.capture());
        captor.getValue();
        return captor.getValue();
    }

    private static Metric getFamilyByKey(List<Metric> families, String key) {
        return families.stream().filter(family -> family.getKey().contains(key)).findFirst().get();
    }

    private static final class MockPullScheduler implements PullScheduler {
        private final boolean timeout;

        public MockPullScheduler(boolean timeout) {
            this.timeout = timeout;
        }

        @Override
        public <T extends Target<Q>, Q extends Query> Cancellation schedulePull(T target, Q query, Dispatcher dispatcher, int periodInSeconds) {
            return null;
        }

        @Override
        public <T extends Target<Q>, Q extends Query> Future<Health> check(HealthCheck<T, Q> check) {
            return new MockFuture(check.call(), !timeout);
        }

        @Override
        public void release(@Nonnull Target<?> target) {

        }
    }

    private static final class MockFuture implements Future<Health> {
        private final Health health;
        private final boolean done;

        public MockFuture(Health health, boolean done) {
            this.health = health;
            this.done = done;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return done;
        }

        @Override
        public Health get() {
            return health;
        }

        @Override
        public Health get(long timeout, TimeUnit unit) {
            return health;
        }
    }
}