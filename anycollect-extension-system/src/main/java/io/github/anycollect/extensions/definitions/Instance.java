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
    @Getter
    private final InjectMode injectMode;
    @Getter
    private final Priority priority;
    @Getter
    private final Scope scope;
    @Getter
    private final String scopeId;

    Instance(final Definition definition,
             final String instanceName,
             final List<Dependency> dependencies,
             final Object resolved) {
        this.definition = definition;
        this.instanceName = instanceName;
        this.dependencies = dependencies;
        this.resolved = resolved;
        this.injectMode = InjectMode.MANUAL;
        this.priority = Priority.OVERRIDE;
        this.scope = Scope.LOCAL;
        this.scopeId = "default";
    }

    public Instance(final Definition definition,
                    final String instanceName,
                    final List<Dependency> dependencies,
                    final Object resolved,
                    final InjectMode injectMode,
                    final Priority priority,
                    final Scope scope,
                    final String scopeId) {
        this.definition = definition;
        this.instanceName = instanceName;
        this.dependencies = dependencies;
        this.resolved = resolved;
        this.injectMode = injectMode;
        this.priority = priority;
        this.scope = scope;
        this.scopeId = scopeId;
    }

    public List<Dependency> getDependencies() {
        return Collections.unmodifiableList(dependencies);
    }

    public Object resolve() {
        return resolved;
    }
}
