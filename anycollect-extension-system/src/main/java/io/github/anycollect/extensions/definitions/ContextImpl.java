package io.github.anycollect.extensions.definitions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class ContextImpl implements ExtendableContext {
    private final List<Instance> instances;

    public ContextImpl() {
        this.instances = new ArrayList<>();
    }

    public ContextImpl(@Nonnull final List<Instance> instances) {
        this.instances = instances;
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
