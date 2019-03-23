package io.github.anycollect.readers.jmx;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.impl.matcher.StaticQueryMatcherResolver;
import io.github.anycollect.core.impl.pull.PullManagerImpl;
import io.github.anycollect.core.impl.self.StdSelfDiscovery;
import io.github.anycollect.extensions.AnnotationDefinitionLoader;
import io.github.anycollect.extensions.DefinitionLoader;
import io.github.anycollect.extensions.InstanceLoader;
import io.github.anycollect.extensions.definitions.Definition;
import io.github.anycollect.extensions.definitions.Instance;
import io.github.anycollect.extensions.snakeyaml.YamlInstanceLoader;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Stat;
import io.github.anycollect.metric.Type;
import io.github.anycollect.readers.jmx.discovery.CurrentApp;
import io.github.anycollect.readers.jmx.query.StaticJmxQueryProvider;
import io.github.anycollect.readers.jmx.utils.HistogramTest;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.File;
import java.io.FileReader;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static io.github.anycollect.assertj.AnyCollectAssertions.assertThat;
import static org.awaitility.Awaitility.await;

class JmxReaderTest {
    private JmxReader jmx;

    @BeforeEach
    void createJmxReader() throws Exception {
        DefinitionLoader definitionLoader = new AnnotationDefinitionLoader(Arrays.asList(
                StdSelfDiscovery.class,
                PullManagerImpl.class,
                CurrentApp.class,
                StaticJmxQueryProvider.class,
                StaticQueryMatcherResolver.class,
                JmxReader.class
        ));
        Collection<Definition> definitions = definitionLoader.load();
        File config = FileUtils.getFile("src", "test", "resources", "jmx-reader.yaml");
        InstanceLoader instanceLoader = new YamlInstanceLoader(new FileReader(config), definitions);
        List<Instance> instances = new ArrayList<>(instanceLoader.load());
        jmx = (JmxReader) instances.get(5).resolve();
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
            List<Metric> families = dispatcher.first;
            assertThat(families).hasSize(1);
            Metric family = families.get(0);
            assertThat(family)
                    .hasTags("instance", "test",
                            "key1", "value1",
                            "k1", "k1val2",
                            "k2", "k2val1")
                    .hasMeta("key2", "value2")
                    .hasMeasurement(Stat.min(), Type.GAUGE, "events", 1.0)
                    .hasMeasurement(Stat.max(), Type.GAUGE, "events", 2.0)
                    .hasMeasurement(Stat.mean(), Type.GAUGE, "events", 3.0)
                    .hasMeasurement(Stat.percentile(50), Type.GAUGE, "events", 50)
                    .hasMeasurement(Stat.percentile(75), Type.GAUGE, "events", 75)
                    .hasMeasurement(Stat.percentile(90), Type.GAUGE, "events", 90)
                    .hasMeasurement(Stat.percentile(95), Type.GAUGE, "events", 95)
                    .hasMeasurement(Stat.percentile(99), Type.GAUGE, "events", 99);
        }
    }

    private static class CumulativeDispatcher implements Dispatcher {
        private volatile List<Metric> first = null;

        @Override
        public void dispatch(@Nonnull Metric metric) {
        }

        @Override
        public void dispatch(@Nonnull List<Metric> metrics) {
            if (first == null) {
                first = metrics;
            }
        }
    }
}