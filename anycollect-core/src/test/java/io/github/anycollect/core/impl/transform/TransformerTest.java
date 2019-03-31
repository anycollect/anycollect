package io.github.anycollect.core.impl.transform;

import io.github.anycollect.core.api.dispatcher.Dispatcher;
import io.github.anycollect.extensions.AnnotationDefinitionLoader;
import io.github.anycollect.extensions.DefinitionLoader;
import io.github.anycollect.extensions.InstanceLoader;
import io.github.anycollect.extensions.definitions.ContextImpl;
import io.github.anycollect.extensions.definitions.Definition;
import io.github.anycollect.extensions.definitions.Instance;
import io.github.anycollect.extensions.snakeyaml.YamlInstanceLoader;
import io.github.anycollect.metric.Metric;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TransformerTest {
    private Transformer transformer;

    @BeforeEach
    void createPullManager() throws Exception {
        DefinitionLoader definitionLoader = new AnnotationDefinitionLoader(Collections.singletonList(Transformer.class));
        Collection<Definition> definitions = definitionLoader.load();
        File config = FileUtils.getFile("src", "test", "resources", "transformer.yaml");
        InstanceLoader instanceLoader = new YamlInstanceLoader(new FileReader(config));
        ContextImpl context = new ContextImpl(definitions);
        instanceLoader.load(context);
        List<Instance> instances = context.getInstances();
        transformer = (Transformer) instances.get(0).resolve();
    }

    @Test
    @DisplayName("is successfully instantiated by extension system")
    void isInstantiatedBySystem() {
        assertThat(transformer).isNotNull();
    }

    @Nested
    class WhenStartTransformer {
        private Dispatcher dispatcher;

        @BeforeEach
        void setUp() {
            dispatcher = mock(Dispatcher.class);
            transformer.start(dispatcher);
        }

        @Test
        void processRenamed() {
            long timestamp = System.currentTimeMillis();
            transformer.submit(Collections.singletonList(
                    Metric.builder()
                            .tag("pid.file", "/home/test/anycollect.pid")
                            .at(timestamp)
                            .build()
            ));
            ArgumentCaptor<Metric> captor = ArgumentCaptor.forClass(Metric.class);
            verify(dispatcher, times(1)).dispatch(captor.capture());
            assertThat(captor.getValue().getFrame())
                    .isEqualTo(Metric.builder()
                            .tag("process", "anycollect")
                            .at(timestamp)
                            .build().getFrame());
        }
    }
}