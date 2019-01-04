package io.github.anycollect.readers.jmx.module;

import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.MetricId;
import io.github.anycollect.metric.Type;
import io.github.anycollect.readers.jmx.application.AllQueryMatcher;
import io.github.anycollect.readers.jmx.application.Application;
import io.github.anycollect.readers.jmx.application.ApplicationRegistry;
import io.github.anycollect.readers.jmx.discovery.CurrentApplicationServerDiscovery;
import io.github.anycollect.readers.jmx.monitoring.JmxMetricRegistry;
import io.github.anycollect.readers.jmx.query.Query;
import io.github.anycollect.readers.jmx.server.Server;
import io.github.anycollect.readers.jmx.server.pool.JmxConnectionPoolFactory;
import io.github.anycollect.readers.jmx.server.pool.impl.CommonsJmxConnectionPoolFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static io.github.anycollect.readers.jmx.monitoring.MonitoringConstants.*;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class JmxReaderQueryModuleTest {
    @Test
    void connectionPoolMetricsTest() throws Exception {
        // prepare server
        JmxConnectionPoolFactory poolFactory = new CommonsJmxConnectionPoolFactory();
        CurrentApplicationServerDiscovery discovery = new CurrentApplicationServerDiscovery("anycollect", poolFactory, new JmxMetricRegistry());
        Application anycollect = new Application("anycollect", new AllQueryMatcher(), false);
        ApplicationRegistry registry = ApplicationRegistry.singleton(anycollect);
        Server current = discovery.getServer(registry);

        JmxReaderQueryModule module = new JmxReaderQueryModule();

        // execute module queries
        Future<List<Metric>> future = Executors.newSingleThreadExecutor().submit(() -> {
            List<Metric> metrics = new ArrayList<>();
            for (Query query : module.getQueries()) {
                metrics.addAll(current.execute(query));
            }
            return metrics;
        });
        await().dontCatchUncaughtExceptions()
                .atMost(100, TimeUnit.MILLISECONDS)
                .pollInterval(10, TimeUnit.MILLISECONDS)
                .until(future::isDone);
        List<Metric> metrics = future.get();

        // assert metrics
        List<MetricId> ids = metrics.stream().map(Metric::getId).collect(toList());
        MetricId.Builder builder = MetricId.builder()
                .unit("connections")
                .tag(APPLICATION_TAG, "anycollect")
                .tag(SERVER_TAG, "anycollect");

        assertThat(ids).containsExactlyInAnyOrder(
                builder.key(CONNECTION_POOL_IDLE).type(Type.GAUGE).build(),
                builder.key(CONNECTION_POOL_ACTIVE).type(Type.GAUGE).build(),
                builder.key(CONNECTION_POOL_INVALIDATED).type(Type.COUNTER).build()
        );
    }
}