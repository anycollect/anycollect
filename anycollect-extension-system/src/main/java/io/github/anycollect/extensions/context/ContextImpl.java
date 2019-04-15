package io.github.anycollect.extensions.context;

import io.github.anycollect.extensions.Definition;
import io.github.anycollect.extensions.Instance;
import io.github.anycollect.extensions.scope.Scope;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public final class ContextImpl implements ExtendableContext {
    private final Map<String, Definition> definitions;
    private final List<Instance> instances;

    public ContextImpl(@Nonnull final Collection<Definition> definitions) {
        this.instances = new ArrayList<>();
        this.definitions = new HashMap<>();
        for (Definition definition : definitions) {
            this.definitions.put(definition.getName(), definition);
        }
    }

    @Override
    public boolean hasInstance(@Nonnull final String name, @Nonnull final Scope scope) {
        return getInstance(name, scope) != null;
    }

    @Nullable
    @Override
    public Instance getInstance(@Nonnull final Class<?> type, @Nonnull final Scope scope) {
        return getInstance(instance -> instance.getDefinition().getExtensionPointClass().equals(type)
                && instance.getInjectMode() == Instance.InjectMode.AUTO, scope);
    }

    @Nullable
    @Override
    public Instance getInstance(@Nonnull final String name, @Nonnull final Scope scope) {
        return getInstance(instance -> instance.getInstanceName().equals(name), scope);
    }

    @Override
    public boolean hasDefinition(@Nonnull final String name) {
        return definitions.containsKey(name);
    }

    @Nullable
    @Override
    public Definition getDefinition(@Nonnull final String name) {
        return definitions.get(name);
    }

    @Override
    public List<Instance> getInstances() {
        return instances;
    }

    private Instance getInstance(@Nonnull final Predicate<Instance> filter, @Nonnull final Scope scope) {
        Instance candidate = null;
        for (Instance instance : instances) {
            if (filter.test(instance)) {
                if (instance.getScope().isParent(scope)) {
                    if (candidate == null) {
                        candidate = instance;
                        continue;
                    }
                    if (instance.getScope().distance(scope) < candidate.getScope().distance(scope)) {
                        candidate = instance;
                    }
                }
            }
        }
        return candidate;
    }

    @Override
    public void addInstance(@Nonnull final Instance instance) {
        this.instances.add(instance);
    }

    @Override
    public void addDefinition(@Nonnull final Definition definition) {
        definitions.put(definition.getName(), definition);
    }
}
