package io.github.anycollect.core.impl.pull;

import io.github.anycollect.core.api.internal.*;
import io.github.anycollect.core.api.query.QueryProvider;
import io.github.anycollect.core.api.target.ServiceDiscovery;
import io.github.anycollect.core.impl.TestQuery;
import io.github.anycollect.core.impl.TestTarget;
import io.github.anycollect.core.impl.pull.StdDesiredStateProvider;
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
    private QueryMatcher<TestTarget, TestQuery> matcher = mock(QueryMatcher.class);
    @SuppressWarnings("unchecked")
    private QueryMatcherResolver<TestTarget, TestQuery> resolver = mock(QueryMatcherResolver.class);

    private StdDesiredStateProvider<TestTarget, TestQuery> desired = new StdDesiredStateProvider<>(
            discovery,
            provider,
            resolver,
            30);

    @Test
    void desiredStateTest() {
        TestTarget target = mock(TestTarget.class);
        TestQuery query1 = new TestQuery("group", "test1");
        TestQuery query2 = new TestQuery("group", "test2");
        when(discovery.discover()).thenReturn(Sets.newLinkedHashSet(target));
        when(provider.provide()).thenReturn(Sets.newLinkedHashSet(query1, query2));
        when(matcher.getPeriodInSeconds(target, query1, 30)).thenReturn(-1);
        when(matcher.getPeriodInSeconds(target, query2, 30)).thenReturn(2);
        when(resolver.current()).thenReturn(matcher);

        State<TestTarget, TestQuery> state = desired.current();
        assertThat(state.getTargets()).containsExactly(target);
        assertThat(state.getQueries(target)).containsExactly(new ImmutablePeriodicQuery<>(query2, 2));
    }

    @Test
    void initTest() {
        desired.init();
        verify(discovery, times(1)).init();
        verify(provider, times(1)).init();
        verify(resolver, times(1)).init();
    }

    @Test
    void destroyTest() {
        desired.destroy();
        verify(discovery, times(1)).destroy();
        verify(provider, times(1)).destroy();
        verify(resolver, times(1)).destroy();
    }
}