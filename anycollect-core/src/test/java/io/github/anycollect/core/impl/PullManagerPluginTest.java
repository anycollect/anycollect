package io.github.anycollect.core.impl;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.core.api.internal.DesiredStateProvider;
import io.github.anycollect.core.api.internal.ImmutableState;
import io.github.anycollect.core.api.internal.PullManager;
import io.github.anycollect.core.api.internal.State;
import io.github.anycollect.core.impl.pull.PullManagerImpl;
import io.github.anycollect.extensions.AnnotationDefinitionLoader;
import io.github.anycollect.extensions.DefinitionLoader;
import io.github.anycollect.extensions.InstanceLoader;
import io.github.anycollect.extensions.definitions.Definition;
import io.github.anycollect.extensions.definitions.Instance;
import io.github.anycollect.extensions.snakeyaml.YamlInstanceLoader;
import io.github.anycollect.metric.Metric;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PullManagerPluginTest {
    private PullManager puller;

    @BeforeEach
    void createPullManager() throws Exception {
        DefinitionLoader definitionLoader = new AnnotationDefinitionLoader(Collections.singletonList(PullManagerImpl.class));
        Collection<Definition> definitions = definitionLoader.load();
        File config = FileUtils.getFile("src", "test", "resources", "anycollect.yaml");
        InstanceLoader instanceLoader = new YamlInstanceLoader(new FileReader(config), definitions);
        List<Instance> instances = new ArrayList<>(instanceLoader.load());
        puller = (PullManager) instances.get(0).resolve();
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
        public void dispatch(@Nonnull Metric family) {

        }

        @Override
        public void dispatch(@Nonnull List<Metric> families) {
            if (this.families == null) {
                this.families = families;
            }
        }
    }
}
