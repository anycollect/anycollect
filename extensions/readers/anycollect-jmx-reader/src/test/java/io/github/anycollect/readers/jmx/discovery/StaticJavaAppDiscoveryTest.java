package io.github.anycollect.readers.jmx.discovery;

import io.github.anycollect.extensions.loaders.AnnotationDefinitionLoader;
import io.github.anycollect.extensions.loaders.DefinitionLoader;
import io.github.anycollect.extensions.loaders.InstanceLoader;
import io.github.anycollect.extensions.context.ContextImpl;
import io.github.anycollect.extensions.Definition;
import io.github.anycollect.extensions.Instance;
import io.github.anycollect.extensions.loaders.snakeyaml.YamlInstanceLoader;
import io.github.anycollect.meter.registry.AnyCollectMeterRegistry;
import io.github.anycollect.readers.jmx.server.JavaApp;
import io.github.anycollect.readers.jmx.server.PooledJavaApp;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class StaticJavaAppDiscoveryTest {
    private StaticJavaAppDiscovery discovery;

    @BeforeEach
    void createPullManager() throws Exception {
        DefinitionLoader definitionLoader = new AnnotationDefinitionLoader(Arrays.asList(
                AnyCollectMeterRegistry.class,
                StaticJavaAppDiscovery.class)
        );
        Collection<Definition> definitions = definitionLoader.load();
        File config = FileUtils.getFile("src", "test", "resources", "static-java-app-discovery.yaml");
        InstanceLoader instanceLoader = new YamlInstanceLoader(new FileReader(config));
        ContextImpl context = new ContextImpl(definitions);
        instanceLoader.load(context);
        List<Instance> instances = context.getInstances();
        discovery = (StaticJavaAppDiscovery) instances.get(1).resolve();
    }

    @Test
    @DisplayName("is successfully instantiated by extension system")
    void isInstantiatedBySystem() {
        assertThat(discovery).isNotNull();
    }

    @Test
    void hasCorrectDiscoveredJavaApps() {
        Set<JavaApp> discover = discovery.discover();
        assertThat(discover).hasSize(1).first()
                .isInstanceOf(PooledJavaApp.class)
                .extracting(JavaApp::getId)
                .isEqualTo("cassandra-1");
    }
}