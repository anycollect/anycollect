package io.github.anycollect.extensions.context;

import io.github.anycollect.extensions.Definition;
import io.github.anycollect.extensions.Instance;
import io.github.anycollect.extensions.scope.Scope;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public abstract class DelegatingContext implements ExtendableContext {
    @Override
    public boolean hasInstance(@Nonnull final String name, @Nonnull final Scope scope) {
        return getContext().hasInstance(name, scope);
    }

    @Nullable
    @Override
    public Instance getInstance(@Nonnull final Class<?> type, @Nonnull final Scope scope) {
        return getContext().getInstance(type, scope);
    }

    @Nullable
    @Override
    public Instance getInstance(@Nonnull final String name, @Nonnull final Scope scope) {
        return getContext().getInstance(name, scope);
    }

    @Override
    public boolean hasDefinition(@Nonnull final String name) {
        return getContext().hasDefinition(name);
    }

    @Nullable
    @Override
    public Definition getDefinition(@Nonnull final String name) {
        return getContext().getDefinition(name);
    }

    @Override
    public List<Instance> getInstances() {
        return getContext().getInstances();
    }

    @Override
    public Collection<Definition> getDefinitions() {
        return getContext().getDefinitions();
    }

    @Override
    public void addInstance(@Nonnull final Instance instance) {
        getContext().addInstance(instance);
    }

    @Override
    public void addDefinition(@Nonnull final Definition definition) {
        getContext().addDefinition(definition);
    }

    protected abstract ExtendableContext getContext();
}
