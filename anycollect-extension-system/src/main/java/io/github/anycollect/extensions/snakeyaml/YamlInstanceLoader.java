package io.github.anycollect.extensions.snakeyaml;

import io.github.anycollect.extensions.VarSubstitutor;
import io.github.anycollect.extensions.InstanceLoader;
import io.github.anycollect.extensions.definitions.ContextImpl;
import io.github.anycollect.extensions.definitions.Definition;
import io.github.anycollect.extensions.definitions.ExtendableContext;
import io.github.anycollect.extensions.definitions.Instance;
import org.yaml.snakeyaml.Yaml;

import java.io.Reader;
import java.util.Collection;

public final class YamlInstanceLoader implements InstanceLoader {
    private final Collection<Definition> extensions;
    private final Reader yamlReader;
    private final ExtendableContext context;
    private final String scopeId;
    private final VarSubstitutor environment;

    public YamlInstanceLoader(final Reader yamlReader,
                              final Collection<Definition> extensions) {
        this("default", yamlReader, extensions, VarSubstitutor.EMPTY);
    }

    public YamlInstanceLoader(final String scopeId,
                              final Reader yamlReader,
                              final Collection<Definition> extensions,
                              final VarSubstitutor environment) {
        this(new ContextImpl(), scopeId, yamlReader, extensions, environment);
    }

    public YamlInstanceLoader(final ExtendableContext context,
                              final String scopeId,
                              final Reader yamlReader,
                              final Collection<Definition> extensions,
                              final VarSubstitutor environment) {
        this.context = context;
        this.scopeId = scopeId;
        this.yamlReader = yamlReader;
        this.extensions = extensions;
        this.environment = environment;
    }

    @Override
    public Collection<Instance> load() {
        CustomConstructor constructor = new CustomConstructor(context, scopeId, extensions, environment);
        Yaml yaml = new Yaml(constructor);
        yaml.load(yamlReader);
        return constructor.getInstances();
    }
}
