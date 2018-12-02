package io.github.anycollect.extensions.snakeyaml;

import io.github.anycollect.extensions.ExtensionInstanceDefinitionLoader;
import io.github.anycollect.extensions.definitions.Definition;
import io.github.anycollect.extensions.definitions.Instance;
import org.yaml.snakeyaml.Yaml;

import java.io.Reader;
import java.util.Collection;

public final class YamlExtensionInstanceDefinitionLoader implements ExtensionInstanceDefinitionLoader {
    private final Collection<Definition> extensions;
    private final Reader yamlReader;

    public YamlExtensionInstanceDefinitionLoader(final Reader yamlReader,
                                                 final Collection<Definition> extensions) {
        this.yamlReader = yamlReader;
        this.extensions = extensions;
    }

    @Override
    public Collection<Instance> load() {
        CustomConstructor constructor = new CustomConstructor(extensions);
        Yaml yaml = new Yaml(constructor);
        yaml.load(yamlReader);
        return constructor.getInstances();
    }
}
