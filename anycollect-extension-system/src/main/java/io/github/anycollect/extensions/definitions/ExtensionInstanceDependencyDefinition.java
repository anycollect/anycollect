package io.github.anycollect.extensions.definitions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ExtensionInstanceDependencyDefinition {
    private final List<ExtensionInstanceDefinition> dependencies;
    private final boolean single;

    public ExtensionInstanceDependencyDefinition(final ExtensionInstanceDefinition dependency) {
        this.dependencies = new ArrayList<>();
        this.dependencies.add(dependency);
        this.single = true;
    }

    public ExtensionInstanceDependencyDefinition(final List<ExtensionInstanceDefinition> dependencies) {
        this.dependencies = dependencies;
        this.single = false;
    }

    public List<ExtensionInstanceDefinition> getInstances() {
        return Collections.unmodifiableList(dependencies);
    }

    public ExtensionInstanceDefinition getInstance() {
        if (!single) {
            throw new IllegalStateException("it's a list dependency");
        }
        return dependencies.get(0);
    }

    public boolean isSingle() {
        return single;
    }
}
