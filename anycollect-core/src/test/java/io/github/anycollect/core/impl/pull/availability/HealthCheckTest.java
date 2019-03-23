package io.github.anycollect.core.impl.pull.availability;

import io.github.anycollect.core.exceptions.ConnectionException;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.core.impl.TestQuery;
import io.github.anycollect.core.impl.TestTarget;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class HealthCheckTest {
    private TestTarget target = mock(TestTarget.class);

    @Test
    void whenNoExceptionsDuringExecutionThenPassedHealthCheck() throws Exception {
        TestQuery query = mock(TestQuery.class);
        when(target.bind(any())).thenCallRealMethod();
        HealthCheck<TestTarget, TestQuery> check = new HealthCheck<>(target, query);
        when(target.execute(query)).thenReturn(Collections.emptyList());
        Health health = check.call();
        assertThat(health).isEqualTo(Health.PASSED);
        verify(target, times(1)).execute(query);
    }

    @Test
    @SuppressWarnings("unchecked")
    void whenExceptionsDuringExecutionThenFailedHealthCheck() throws Exception {
        TestQuery query = mock(TestQuery.class);
        when(target.bind(any())).thenCallRealMethod();
        HealthCheck<TestTarget, TestQuery> check = new HealthCheck<>(target, query);
        when(target.execute(query)).thenThrow(QueryException.class, ConnectionException.class, RuntimeException.class);
        Health health1 = check.call();
        Health health2 = check.call();
        Health health3 = check.call();
        assertThat(health1).isEqualTo(Health.FAILED);
        assertThat(health2).isEqualTo(Health.FAILED);
        assertThat(health3).isEqualTo(Health.FAILED);
        verify(target, times(3)).execute(query);
    }
}