package io.github.anycollect.extensions.context;

import io.github.anycollect.extensions.Definition;
import io.github.anycollect.extensions.Instance;
import io.github.anycollect.extensions.annotations.InjectMode;
import io.github.anycollect.extensions.scope.Scope;
import io.github.anycollect.extensions.scope.SimpleScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public final class ContextImpl implements ExtendableContext {
    private static final Logger LOG = LoggerFactory.getLogger(ContextImpl.class);
    private final Map<String, Definition> definitions;
    private final List<Instance> instances;
    private final Scope auto = new SimpleScope(null, "auto");

    public ContextImpl() {
        this(Collections.emptyList());
    }

    public ContextImpl(@Nonnull final Collection<Definition> definitions) {
        this.instances = new ArrayList<>();
        this.definitions = new HashMap<>();
        for (Definition definition : definitions) {
            addDefinition(definition);
        }
    }

    @Override
    public boolean hasInstance(@Nonnull final String name, @Nonnull final Scope scope) {
        return getInstance(name, scope) != null;
    }

    @Nullable
    @Override
    public Instance getInstance(@Nonnull final Class<?> type, @Nonnull final Scope scope) {
        return getInstance(instance -> instance.getDefinition().getContracts().contains(type)
                && instance.getInjectMode() == InjectMode.AUTO, scope);
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

    @Override
    public Collection<Definition> getDefinitions() {
        return definitions.values();
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
                } else if (instance.getScope().equals(auto)) {
                    if (candidate == null) {
                        candidate = instance;
                    }
                }
            }
        }
        return candidate;
    }

    @Override
    public void addInstance(@Nonnull final Instance instance) {
        // TODO check if name is unique in the scope
        this.instances.add(instance);
    }

    @Override
    public void addDefinition(@Nonnull final Definition definition) {
        definitions.put(definition.getName(), definition);
        if (definition.isAutoLoad()) {
            LOG.debug("auto load instance of {}", definition.getName());
            addInstance(definition.createAutoInstance(auto));
        }
    }
}
