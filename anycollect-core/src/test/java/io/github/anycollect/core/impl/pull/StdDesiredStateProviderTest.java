package io.github.anycollect.core.impl.pull;

import io.github.anycollect.core.api.internal.ImmutablePeriodicQuery;
import io.github.anycollect.core.api.internal.QueryMatcher;
import io.github.anycollect.core.api.internal.QueryMatcherResolver;
import io.github.anycollect.core.api.internal.State;
import io.github.anycollect.core.api.query.QueryProvider;
import io.github.anycollect.core.api.target.ServiceDiscovery;
import io.github.anycollect.core.impl.TestQuery;
import io.github.anycollect.core.impl.TestTarget;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class StdDesiredStateProviderTest {
    @SuppressWarnings("unchecked")
    private ServiceDiscovery<TestTarget> discovery = mock(ServiceDiscovery.class);
    @SuppressWarnings("unchecked")
    private QueryProvider<TestQuery> provider = mock(QueryProvider.class);
    @SuppressWarnings("unchecked")
    private QueryMatcher matcher = mock(QueryMatcher.class);
    private QueryMatcherResolver resolver = mock(QueryMatcherResolver.class);

    private StdDesiredStateProvider<TestTarget, TestQuery> desired = new StdDesiredStateProvider<>(
            discovery,
            provider,
            resolver,
            30);

    @Test
    void desiredStateTest() {
        TestTarget target = mock(TestTarget.class);
        TestQuery query1 = new TestQuery("id1");
        TestQuery query2 = new TestQuery("id2");
        when(discovery.discover()).thenReturn(Sets.newLinkedHashSet(target));
        when(provider.provide()).thenReturn(Sets.newLinkedHashSet(query1, query2));
        when(matcher.getPeriodInSeconds(target, query1, 30)).thenReturn(-1);
        when(matcher.getPeriodInSeconds(target, query2, 30)).thenReturn(2);
        when(resolver.current()).thenReturn(matcher);

        State<TestTarget, TestQuery> state = desired.current();
        assertThat(state.getTargets()).containsExactly(target);
        assertThat(state.getQueries(target)).containsExactly(new ImmutablePeriodicQuery<>(query2, 2));
    }
}