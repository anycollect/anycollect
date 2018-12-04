package io.github.anycollect.extensions;

import io.github.anycollect.extensions.definitions.Definition;
import io.github.anycollect.extensions.definitions.Instance;
import io.github.anycollect.extensions.samples.Configs;
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
        AnnotationDefinitionLoader definitionLoader = new AnnotationDefinitionLoader(Collections.singletonList(Configs.ListOfStringsWithKey.class));
        Collection<Definition> definitions = definitionLoader.load();
        YamlInstanceLoader instanceLoader = new YamlInstanceLoader(
                new StringReader(TestConfigUtils.read("config/anycollect-custom-config-key.yaml")),
                definitions);
        Collection<Instance> instances = instanceLoader.load();
        assertThat(instances).hasSize(1);
        Instance instance = instances.iterator().next();
        Configs.ListOfStringsWithKey extension = (Configs.ListOfStringsWithKey) instance.resolve();
        assertThat(extension.getAliases()).containsExactly("alias1", "alias2");
    }
}
