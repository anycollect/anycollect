package io.github.anycollect.extensions.definitions;

import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;

@ToString
public final class Instance {
    @Getter
    private final Definition definition;
    @Getter
    private final String instanceName;
    private final List<Dependency> dependencies;
    private final Object resolved;

    Instance(final Definition definition,
             final String instanceName,
             final List<Dependency> dependencies,
             final Object resolved) {
        this.definition = definition;
        this.instanceName = instanceName;
        this.dependencies = dependencies;
        this.resolved = resolved;
    }

    public List<Dependency> getDependencies() {
        return Collections.unmodifiableList(dependencies);
    }

    public Object resolve() {
        return resolved;
    }
}
