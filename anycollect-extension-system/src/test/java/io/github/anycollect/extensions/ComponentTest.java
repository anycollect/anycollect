package io.github.anycollect.extensions;

import io.github.anycollect.extensions.definitions.Definition;
import io.github.anycollect.extensions.definitions.Instance;
import io.github.anycollect.extensions.samples.Configs;
import io.github.anycollect.extensions.samples.ExtensionPointWithId;
import io.github.anycollect.extensions.snakeyaml.YamlInstanceLoader;
import io.github.anycollect.extensions.utils.TestConfigUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class ComponentTest {
    @Test
    @DisplayName("extension can retrieve specific part of config by key")
    void extensionCanRetrieveSpecificPartOfConfigByKey() throws IOException {
        Collection<Instance> instances = load(Configs.ListOfStringsWithKey.class, "config/anycollect-custom-config-key.yaml");
        assertThat(instances).hasSize(1);
        Instance instance = instances.iterator().next();
        Configs.ListOfStringsWithKey extension = (Configs.ListOfStringsWithKey) instance.resolve();
        assertThat(extension.getAliases()).containsExactly("alias1", "alias2");
    }

    @Test
    @DisplayName("extension can get own instance id")
    void extensionCanGetOwnInstanceId() throws IOException {
        Collection<Instance> instances = load(ExtensionPointWithId.class, "config/anycollect-instance-id.yaml");
        assertThat(instances).hasSize(1);
        ExtensionPointWithId extension = (ExtensionPointWithId) instances.iterator().next().resolve();
        assertThat(extension.getId()).isEqualTo("withId");
    }

    private static Collection<Instance> load(Class<?> extension, String filename) throws IOException {
        AnnotationDefinitionLoader definitionLoader = new AnnotationDefinitionLoader(Collections.singletonList(extension));
        Collection<Definition> definitions = definitionLoader.load();
        YamlInstanceLoader instanceLoader = new YamlInstanceLoader(
                new StringReader(TestConfigUtils.read(filename)),
                definitions);
        return instanceLoader.load();
    }
}
