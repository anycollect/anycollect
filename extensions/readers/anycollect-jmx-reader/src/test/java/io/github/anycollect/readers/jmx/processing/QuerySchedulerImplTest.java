package io.github.anycollect.readers.jmx.processing;

import com.google.common.collect.Sets;
import io.github.anycollect.metric.ImmutableMetric;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.readers.jmx.application.Application;
import io.github.anycollect.readers.jmx.application.QueryMatcher;
import io.github.anycollect.readers.jmx.query.Query;
import io.github.anycollect.readers.jmx.server.JmxConnection;
import io.github.anycollect.readers.jmx.server.Server;
import io.github.anycollect.readers.jmx.server.SimpleServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class QuerySchedulerImplTest {
    private ScheduledExecutorService service = mock(ScheduledExecutorService.class);
    private QueryExecutor executor = mock(QueryExecutor.class);
    private Duration initialDelay = Duration.ZERO;
    private Duration defaultInterval = Duration.ofMillis(10);
    private QuerySchedulerImpl scheduler = new QuerySchedulerImpl(
            service,
            executor,
            initialDelay,
            defaultInterval
    );
    private Metric metric = mock(ImmutableMetric.class);
    private List<Metric> metrics = Collections.singletonList(metric);
    private Map<QuerySubmitJob, List<ScheduledFuture<?>>> jobs = new HashMap<>();

    @BeforeEach
    void setUp() {
        when(service.scheduleAtFixedRate(any(), anyLong(), anyLong(), any())).then(new Answer<ScheduledFuture<?>>() {
            @Override
            public ScheduledFuture<?> answer(InvocationOnMock invocation) throws Throwable {
                QuerySubmitJob job = invocation.getArgument(0);
                ScheduledFuture<?> future = mock(ScheduledFuture.class);
                List<ScheduledFuture<?>> futures = jobs.computeIfAbsent(job, j -> new ArrayList<>());
                futures.add(future);
                return future;
            }
        });
    }

    @Test
    void submitJobSimpleTest() throws Exception {
        Query query = mock(Query.class);
        when(query.executeOn(any())).thenReturn(metrics);
        Server server = server("1", "app");
        match(server, query);
        scheduler.schedule(
                Sets.newHashSet(server),
                Sets.newHashSet(query)
        );
        verifySubmitOnce(job(server, query));
    }

    @Test
    void changeListScheduledServersTest() {
        Query appQuery1 = mock(Query.class);
        Query appQuery2 = mock(Query.class);
        Application app = app("app");
        match(app, appQuery1);
        match(app, appQuery2);
        Server app1 = server("1", app);
        Server app2 = server("2", app);
        Server app3 = server("3", app);
        Server app4 = server("4", app);
        scheduler.schedule(
                Sets.newHashSet(app1, app2),
                Sets.newHashSet(appQuery1, appQuery2)
        );
        verifySubmitOnce(job(app1, appQuery1));
        verifySubmitOnce(job(app1, appQuery2));
        verifySubmitOnce(job(app2, appQuery1));
        verifySubmitOnce(job(app2, appQuery2));
        scheduler.schedule(
                Sets.newHashSet(app3, app4),
                Sets.newHashSet(appQuery1, appQuery2)
        );
        verifyCancelOnce(job(app1, appQuery1));
        verifyCancelOnce(job(app1, appQuery2));
        verifyCancelOnce(job(app2, appQuery1));
        verifyCancelOnce(job(app2, appQuery2));
        verifySubmitOnce(job(app3, appQuery1));
        verifySubmitOnce(job(app3, appQuery2));
        verifySubmitOnce(job(app4, appQuery1));
        verifySubmitOnce(job(app4, appQuery2));
    }

    @Test
    void mustNotResubmitAlreadyScheduledJobs() {
        Query appQuery1 = mock(Query.class);
        Query appQuery2 = mock(Query.class);
        Application app = app("app");
        match(app, appQuery1);
        match(app, appQuery2);
        Server app1 = server("1", app);
        Server app2 = server("2", app);
        scheduler.schedule(
                Sets.newHashSet(app1),
                Sets.newHashSet(appQuery1, appQuery2)
        );
        verifySubmitOnce(job(app1, appQuery1));
        scheduler.schedule(
                Sets.newHashSet(app1, app2),
                Sets.newHashSet(appQuery1, appQuery2)
        );
        verifySubmitOnce(job(app1, appQuery1));
        verifySubmitOnce(job(app1, appQuery2));
        verifySubmitOnce(job(app2, appQuery1));
        verifySubmitOnce(job(app2, appQuery2));
        verifyNotCancel(job(app1, appQuery1));
        verifyNotCancel(job(app1, appQuery2));
    }

    private QuerySubmitJob job(Server server, Query query) {
        return new QuerySubmitJob(query, server, executor);
    }

    private void verifyNotCancel(QuerySubmitJob job) {
        assertThat(jobs.get(job)).hasSize(1);
        verify(jobs.get(job).get(0), never()).cancel(anyBoolean());
    }

    private void verifyCancelOnce(QuerySubmitJob job) {
        assertThat(jobs.get(job)).hasSize(1);
        verify(jobs.get(job).get(0), times(1)).cancel(false);
    }

    private void verifySubmitOnce(QuerySubmitJob job) {
        assertThat(jobs.get(job)).hasSize(1);
        verify(service, times(1)).scheduleAtFixedRate(eq(job), eq(0L), eq(10L), eq(TimeUnit.MILLISECONDS));
    }

    private static void match(Application application, Query query) {
        when(application.getQueryMatcher().matches(query)).thenReturn(true);
    }

    private static void match(Server server, Query query) {
        when(server.getApplication().getQueryMatcher().matches(query)).thenReturn(true);
    }

    private static Server server(String id, String appName) {
        return new SimpleServer(id, app(appName), JmxConnection.local());
    }

    private static Server server(String id, Application application) {
        return new SimpleServer(id, application, JmxConnection.local());
    }

    private static Application app(String name) {
        return new Application(name, mock(QueryMatcher.class), false);
    }
}