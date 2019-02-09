package io.github.anycollect.core.impl;

import io.github.anycollect.core.api.internal.PullManager;
import io.github.anycollect.core.impl.pull.PullManagerImpl;
import io.github.anycollect.extensions.AnnotationDefinitionLoader;
import io.github.anycollect.extensions.DefinitionLoader;
import io.github.anycollect.extensions.InstanceLoader;
import io.github.anycollect.extensions.definitions.Definition;
import io.github.anycollect.extensions.definitions.Instance;
import io.github.anycollect.extensions.snakeyaml.YamlInstanceLoader;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PullManagerPluginTest {
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
}
