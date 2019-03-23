package io.github.anycollect.extensions.definitions;

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
    public boolean hasInstance(@Nonnull final String name, @Nonnull final String scopeId) {
        return getInstance(name, scopeId) != null;
    }

    @Nullable
    @Override
    public Instance getInstance(@Nonnull final Class<?> type, @Nonnull final String scopeId) {
        return getInstance(instance -> instance.getDefinition().getExtensionPointClass().equals(type)
                && instance.getInjectMode() == InjectMode.AUTO, scopeId);
    }

    @Nullable
    @Override
    public Instance getInstance(@Nonnull final String name, @Nonnull final String scopeId) {
        return getInstance(instance -> instance.getInstanceName().equals(name), scopeId);
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

    private Instance getInstance(@Nonnull final Predicate<Instance> filter, @Nonnull final String scopeId) {
        Instance candidate = null;
        for (Instance instance : instances) {
            if (filter.test(instance)) {
                if (instance.getScope() == Scope.GLOBAL || instance.getScopeId().equals(scopeId)) {
                    if (candidate == null) {
                        candidate = instance;
                        continue;
                    }
                    Priority priority = instance.getPriority();
                    if (priority.isHigherThan(candidate.getPriority())) {
                        candidate = instance;
                    }
                    if (priority.isLowerThan(candidate.getPriority())) {
                        continue;
                    }
                    if (candidate.getScopeId().equals(scopeId) && !instance.getScopeId().equals(scopeId)) {
                        continue;
                    }
                    if (!candidate.getScopeId().equals(scopeId) && instance.getScopeId().equals(scopeId)) {
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
}
