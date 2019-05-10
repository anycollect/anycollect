package io.github.anycollect.extensions;

import io.github.anycollect.extensions.annotations.InjectMode;
import io.github.anycollect.extensions.scope.Scope;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nonnull;

@ToString
public final class Instance {
    @Getter
    private final Definition definition;
    @Getter
    private final String instanceName;
    private final Object resolved;
    @Getter
    private final InjectMode injectMode;
    @Getter
    private final Scope scope;
    @Getter
    private final boolean copy;

    public Instance(final Definition definition,
                    final String instanceName,
                    final Object resolved,
                    final InjectMode injectMode,
                    final Scope scope) {
        this(definition, instanceName, resolved, injectMode, scope, false);
    }

    public Instance(final Definition definition,
                    final String instanceName,
                    final Object resolved,
                    final InjectMode injectMode,
                    final Scope scope,
                    final boolean copy) {
        this.definition = definition;
        this.instanceName = instanceName;
        this.resolved = resolved;
        this.injectMode = injectMode;
        this.scope = scope;
        this.copy = copy;
    }

    public Object resolve() {
        return resolved;
    }

    public Instance copy(@Nonnull final Scope scope) {
        return new Instance(definition, instanceName, resolved, injectMode, scope, true);
    }

}
