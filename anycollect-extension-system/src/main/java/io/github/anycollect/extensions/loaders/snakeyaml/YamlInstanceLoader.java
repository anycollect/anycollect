package io.github.anycollect.extensions.loaders.snakeyaml;

import io.github.anycollect.extensions.substitution.EnvVarSubstitutor;
import io.github.anycollect.extensions.loaders.InstanceLoader;
import io.github.anycollect.extensions.substitution.VarSubstitutor;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.ExtDependency;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.extensions.context.ExtendableContext;
import io.github.anycollect.extensions.scope.FileScope;
import io.github.anycollect.extensions.scope.Scope;
import io.github.anycollect.extensions.scope.SimpleScope;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Nonnull;
import java.io.Reader;

@Extension(name = YamlInstanceLoader.NAME, point = InstanceLoader.class)
public final class YamlInstanceLoader implements InstanceLoader {
    public static final String NAME = "YamlLoader";
    private final Scope scope;
    private final Reader yamlReader;
    private final VarSubstitutor environment;


    public YamlInstanceLoader(final Reader yamlReader) {
        this(new SimpleScope(null, "default"), yamlReader, new EnvVarSubstitutor());
    }

    // TODO
    @ExtCreator
    public YamlInstanceLoader(@ExtDependency(qualifier = "parentLoader") @Nonnull final InstanceLoader parentLoader) {
        this(FileScope.child(parentLoader.getScope(), null), null, null);
    }

    public YamlInstanceLoader(final Scope scope,
                              final Reader yamlReader,
                              final VarSubstitutor environment) {
        this.scope = scope;
        this.yamlReader = yamlReader;
        this.environment = environment;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public void load(@Nonnull final ExtendableContext context) {
        CustomConstructor constructor = new CustomConstructor(context, scope, environment);
        Yaml yaml = new Yaml(constructor);
        yaml.load(yamlReader);
    }

    @Override
    public String toString() {
        return scope.toString();
    }
}
