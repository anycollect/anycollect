package io.github.anycollect.readers.jmx;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.metric.*;
import io.github.anycollect.readers.jmx.utils.HistogramTest;
import io.github.anycollect.test.TestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.anycollect.assertj.AnyCollectAssertions.assertThat;
import static org.awaitility.Awaitility.await;

class JmxReaderTest {
    private JmxReader jmx;

    @BeforeEach
    void createJmxReader() throws Exception {
        TestContext context = new TestContext("jmx-reader.yaml");
        jmx = (JmxReader) context.getInstance("jmx").resolve();
    }

    @Test
    @DisplayName("is successfully instantiated by extension system")
    void isInstantiatedBySystem() {
        assertThat(jmx).isNotNull();
    }

    @Nested
    class QueryTest {
        private CumulativeDispatcher dispatcher = new CumulativeDispatcher();

        @BeforeEach
        void setUp() throws Exception {
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            server.registerMBean(new HistogramTest(), new ObjectName("test:name=Test,k1=k1val1,k2=k2val1"));
            server.registerMBean(new HistogramTest(), new ObjectName("test:name=Test,k1=k1val1,k2=k2val2"));
            server.registerMBean(new HistogramTest(), new ObjectName("test:name=Test,k1=k1val2,k2=k2val1"));
            server.registerMBean(new HistogramTest(), new ObjectName("test:name=Test,k1=k1val2,k2=k2val2"));
        }

        @Test
        void mbeanHasBeenQueried() {
            jmx.start(dispatcher);
            await().until(() -> dispatcher.first != null);
            List<Sample> samples = dispatcher.first;
            assertThat(samples).hasSize(9);
            samples = samples.stream()
                    .map(sample -> sample.getMetric().sample(sample.getValue(), 0))
                    .collect(Collectors.toList());
            Tags tags = Tags.of("key1", "value1",
                    "k1", "k1val2",
                    "k2", "k2val1");
            Tags meta = Tags.of("key2", "value2");
            Metric.Factory builder = Metric.builder()
                    .key("histogram")
                    .tags(tags)
                    .meta(meta);
            assertThat(samples)
                    .contains(builder.min("events").sample(1.0, 0))
                    .contains(builder.max("events").sample(2.0, 0))
                    .contains(builder.mean("events").sample(3.0, 0))
                    .contains(builder.percentile(50, "events").sample(50, 0))
                    .contains(builder.percentile(75, "events").sample(75, 0))
                    .contains(builder.percentile(90, "events").sample(90, 0))
                    .contains(builder.percentile(95, "events").sample(95, 0))
                    .contains(builder.percentile(99, "events").sample(99, 0));
        }
    }

    private static class CumulativeDispatcher implements Dispatcher {
        private volatile List<Sample> first = null;

        @Override
        public void dispatch(@Nonnull Sample sample) {
        }

        @Override
        public void dispatch(@Nonnull List<Sample> samples) {
            if (first == null) {
                first = samples;
            }
        }
    }
}