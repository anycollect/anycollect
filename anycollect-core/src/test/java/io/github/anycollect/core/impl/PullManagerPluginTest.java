package io.github.anycollect.core.impl;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.internal.DesiredStateProvider;
import io.github.anycollect.core.api.internal.ImmutableState;
import io.github.anycollect.core.api.internal.PullManager;
import io.github.anycollect.core.api.internal.State;
import io.github.anycollect.core.impl.pull.PullManagerImpl;
import io.github.anycollect.core.impl.self.StdSelfDiscovery;
import io.github.anycollect.extensions.loaders.AnnotationDefinitionLoader;
import io.github.anycollect.extensions.loaders.DefinitionLoader;
import io.github.anycollect.extensions.loaders.InstanceLoader;
import io.github.anycollect.extensions.context.ContextImpl;
import io.github.anycollect.extensions.Definition;
import io.github.anycollect.extensions.Instance;
import io.github.anycollect.extensions.loaders.snakeyaml.YamlInstanceLoader;
import io.github.anycollect.metric.Metric;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileReader;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PullManagerPluginTest {
    private PullManager puller;

    @BeforeEach
    void createPullManager() throws Exception {
        DefinitionLoader definitionLoader = new AnnotationDefinitionLoader(Arrays.asList(StdSelfDiscovery.class, PullManagerImpl.class));
        Collection<Definition> definitions = definitionLoader.load();
        File config = FileUtils.getFile("src", "test", "resources", "anycollect.yaml");
        InstanceLoader instanceLoader = new YamlInstanceLoader(new FileReader(config));
        ContextImpl context = new ContextImpl(definitions);
        instanceLoader.load(context);
        List<Instance> instances = context.getInstances();
        puller = (PullManager) instances.get(1).resolve();
    }

    @Test
    @DisplayName("is successfully instantiated by extension system")
    void isInstantiatedBySystem() {
        assertThat(puller).isNotNull();
    }

    @Test
    @DisplayName("health checking timeseries is generated")
    void healthChecking() {
        TestTarget target1 = mock(TestTarget.class);
        when(target1.getId()).thenReturn("app1");
        TestQuery healthQuery = mock(TestQuery.class);
        State<TestTarget, TestQuery> state = ImmutableState.<TestTarget, TestQuery>builder()
                .put(target1)
                .build();
        @SuppressWarnings("unchecked")
        DesiredStateProvider<TestTarget, TestQuery> provider = mock(DesiredStateProvider.class);
        when(provider.current()).thenReturn(state);
        FirstDispatch dispatcher = new FirstDispatch();
        puller.start(provider, dispatcher, healthQuery);
        await().until(() -> dispatcher.families != null);
        List<Metric> families = dispatcher.families;
        assertThat(families.stream().map(Metric::getKey))
                .containsExactlyInAnyOrder(
                        "instances.up",
                        "instances.down",
                        "instances.timeout",
                        "instances.desired");
    }

    private static final class FirstDispatch implements Dispatcher {
        private volatile List<Metric> families = null;

        @Override
        public void dispatch(@Nonnull Metric metric) {

        }

        @Override
        public void dispatch(@Nonnull List<Metric> metrics) {
            if (this.families == null) {
                this.families = metrics;
            }
        }
    }
}
