package io.github.anycollect.readers.jmx.processing;

import com.google.common.collect.Sets;
import io.github.anycollect.readers.jmx.application.ApplicationRegistry;
import io.github.anycollect.readers.jmx.application.DynamicApplicationRegistry;
import io.github.anycollect.readers.jmx.discovery.ServerDiscovery;
import io.github.anycollect.readers.jmx.module.QueryModule;
import io.github.anycollect.readers.jmx.query.Query;
import io.github.anycollect.readers.jmx.server.Server;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.mockito.Mockito.*;

class QuerySchedulerUpdateJobTest {
    private QueryScheduler scheduler = mock(QueryScheduler.class);
    private DynamicApplicationRegistry dynamicRegistry = mock(DynamicApplicationRegistry.class);
    private ServerDiscovery discovery = mock(ServerDiscovery.class);
    private QueryModule module = mock(QueryModule.class);
    private ApplicationRegistry registry = ApplicationRegistry.empty();
    private QuerySchedulerUpdateJob job = new QuerySchedulerUpdateJob(
            scheduler,
            dynamicRegistry,
            discovery,
            module
    );

    @Test
    void successTest() throws Exception {
        Set<Server> servers = Sets.newHashSet();
        Set<Query> queries = Sets.newHashSet();
        when(dynamicRegistry.getCurrentSnapshot()).thenReturn(registry);
        when(module.getQueries()).thenReturn(queries);
        when(discovery.getServers(registry)).thenReturn(servers);
        job.run();
        verify(scheduler, times(1)).schedule(servers, queries);
    }

    @Test
    void mustHandleAnyExceptionAndWillNotUpdateScheduler() {
        when(dynamicRegistry.getCurrentSnapshot()).thenReturn(registry);
        when(module.getQueries()).thenThrow(RuntimeException.class);
        job.run();
        verify(scheduler, never()).schedule(anySet(), anySet());
    }
}