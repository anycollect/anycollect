package io.github.anycollect.extensions.snakeyaml;

import io.github.anycollect.extensions.ExtensionInstanceDefinitionLoader;
import io.github.anycollect.extensions.definitions.ExtensionDefinition;
import io.github.anycollect.extensions.definitions.ExtensionInstanceDefinition;
import org.yaml.snakeyaml.Yaml;

import java.io.Reader;
import java.util.Collection;

public final class YamlExtensionInstanceDefinitionLoader implements ExtensionInstanceDefinitionLoader {
    private final Collection<ExtensionDefinition> extensions;
    private final Reader yamlReader;

    public YamlExtensionInstanceDefinitionLoader(final Reader yamlReader,
                                                 final Collection<ExtensionDefinition> extensions) {
        this.yamlReader = yamlReader;
        this.extensions = extensions;
    }

    @Override
    public Collection<ExtensionInstanceDefinition> load() {
        CustomConstructor constructor = new CustomConstructor(extensions);
        Yaml yaml = new Yaml(constructor);
        yaml.load(yamlReader);
        return constructor.getInstances();
    }
}
