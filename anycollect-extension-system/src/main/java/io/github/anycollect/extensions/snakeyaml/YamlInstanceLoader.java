package io.github.anycollect.extensions.snakeyaml;

import io.github.anycollect.extensions.EnvVarSubstitutor;
import io.github.anycollect.extensions.InstanceLoader;
import io.github.anycollect.extensions.VarSubstitutor;
import io.github.anycollect.extensions.definitions.ExtendableContext;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Nonnull;
import java.io.Reader;

public final class YamlInstanceLoader implements InstanceLoader {
    private final Reader yamlReader;
    private final String scopeId;
    private final VarSubstitutor environment;

    public YamlInstanceLoader(final Reader yamlReader) {
        this("default", yamlReader, new EnvVarSubstitutor());
    }

    public YamlInstanceLoader(final String scopeId,
                              final Reader yamlReader,
                              final VarSubstitutor environment) {
        this.scopeId = scopeId;
        this.yamlReader = yamlReader;
        this.environment = environment;
    }

    @Override
    public void load(@Nonnull final ExtendableContext context) {
        CustomConstructor constructor = new CustomConstructor(context, scopeId, environment);
        Yaml yaml = new Yaml(constructor);
        yaml.load(yamlReader);
    }
}
