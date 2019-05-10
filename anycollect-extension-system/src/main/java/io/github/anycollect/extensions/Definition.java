package io.github.anycollect.extensions;

import io.github.anycollect.core.exceptions.ConfigurationException;
import io.github.anycollect.extensions.annotations.InjectMode;
import io.github.anycollect.extensions.context.Context;
import io.github.anycollect.extensions.dependencies.*;
import io.github.anycollect.extensions.di.Instantiator;
import io.github.anycollect.extensions.exceptions.ExtensionCreationException;
import io.github.anycollect.extensions.scope.Scope;
import io.github.anycollect.extensions.scope.SimpleScope;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

@ToString
@EqualsAndHashCode
public final class Definition {
    @Getter
    private final String name;
    @Getter
    private final Class<?> extensionPointClass;
    @Getter
    private final Class<?> extensionClass;
    private final ConfigDefinition configDefinition;
    private final Map<String, SingleDependencyDefinition> singleDeps;
    private final Map<String, MultiDependencyDefinition> multiDeps;
    private final AutoLoad autoLoad;
    private final Instantiator instantiator;

    public static Builder builder() {
        return new Builder();
    }

    private Definition(final Builder builder) {
        this.name = builder.name;
        this.extensionPointClass = builder.extensionPointClass;
        this.extensionClass = builder.extensionClass;
        this.singleDeps = new HashMap<>();
        this.multiDeps = new HashMap<>();
        this.configDefinition = builder.configDefinition;
        if (Modifier.isAbstract(extensionClass.getModifiers())
                || Modifier.isInterface(extensionClass.getModifiers())) {
            throw new ConfigurationException("extension class must not be abstract or interface" + extensionClass);
        }
        if (!Modifier.isPublic(extensionClass.getModifiers())) {
            throw new ConfigurationException("constructor " + extensionClass + " must be public");
        }
        this.instantiator = Instantiator.forConstructor(builder.constructor);
        for (SingleDependencyDefinition definition : builder.singleDependencyDefinitions) {
            singleDeps.put(definition.getName(), definition);
        }
        for (MultiDependencyDefinition definition : builder.multiDependencyDefinitions) {
            multiDeps.put(definition.getName(), definition);
        }
        this.autoLoad = builder.autoLoad;
    }

    public Optional<ConfigDefinition> getConfigDefinition() {
        return Optional.ofNullable(configDefinition);
    }

    public Instance createInstance(final String instanceName) {
        return createInstance(instanceName, null);
    }

    public Instance createInstance(final String instanceName, final Object config) {
        return createInstance(instanceName, config, Collections.emptyMap(), Collections.emptyMap());
    }

    public Instance createInstance(final String instanceName,
                                   final Object config,
                                   final Map<String, Instance> singleDependencies,
                                   final Map<String, List<Instance>> multiDependencies) {
        return createInstance(instanceName, config, singleDependencies, multiDependencies,
                Context.EMPTY, InjectMode.MANUAL, new SimpleScope(null, "default"));
    }

    public Instance createInstance(final String instanceName,
                                   final Context context,
                                   final InjectMode injectMode,
                                   final Scope scope) {
        return createInstance(instanceName,
                null,
                Collections.emptyMap(),
                Collections.emptyMap(),
                context,
                injectMode,
                scope);
    }

    public boolean isAutoLoad() {
        return autoLoad.enabled;
    }

    public Instance createAutoInstance(final Scope scope) {
        if (!autoLoad.enabled) {
            throw new IllegalStateException("auto load is not supported");
        }
        return createInstance(autoLoad.instanceName,
                null,
                Collections.emptyMap(),
                Collections.emptyMap(),
                null,
                autoLoad.injectMode,
                scope);
    }

    public Instance createInstance(final String instanceName,
                                   final Object config,
                                   final Map<String, Instance> singleDependencies,
                                   final Map<String, List<Instance>> multiDependencies,
                                   final Context context,
                                   final InjectMode injectMode,
                                   final Scope scope) {
        List<Dependency> dependencies = new ArrayList<>();
        if (configDefinition != null) {
            dependencies.add(configDefinition.create(config));
        } else if (config != null) {
            throw new ConfigurationException("configuration is not supported by " + name + " config: " + config);
        }
        for (String dependencyName : singleDeps.keySet()) {
            SingleDependencyDefinition definition = singleDeps.get(dependencyName);
            if (dependencyName.equals("__instanceId__")) {
                dependencies.add(new SimpleDependency(instanceName, definition.getPosition()));
                continue;
            }
            Instance instance = singleDependencies.get(dependencyName);
            if (instance == null) {
                instance = context.getInstance(definition.getParameterType(), scope);
            }
            Dependency dependency = definition.create(instance);
            dependencies.add(dependency);
        }
        for (String dependencyName : multiDeps.keySet()) {
            MultiDependencyDefinition definition = multiDeps.get(dependencyName);
            List<Instance> instances = multiDependencies.get(dependencyName);
            if (instances == null) {
                instances = Collections.emptyList();
            }
            Dependency dependency = definition.create(instances);
            dependencies.add(dependency);
        }
        try {
            Object resolved = instantiator.instantiate(dependencies);
            return new Instance(this, instanceName, resolved, injectMode, scope);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new IllegalStateException("could not instantiate extension from " + instantiator
                    + " using " + dependencies, e);
        } catch (InvocationTargetException e) {
            throw new ExtensionCreationException(this, dependencies, e.getCause());
        }
    }

    @Getter
    @EqualsAndHashCode
    public static class AutoLoad {
        private final String instanceName;
        private final InjectMode injectMode;
        private final boolean enabled;

        public static AutoLoad disabled() {
            return new AutoLoad("", InjectMode.AUTO, false);
        }

        public AutoLoad(final String instanceName, final InjectMode injectMode, final boolean enabled) {
            this.instanceName = instanceName;
            this.injectMode = injectMode;
            this.enabled = enabled;
        }
    }

    public static final class Builder {
        private String name;
        private Class<?> extensionPointClass;
        private Class<?> extensionClass;
        private ConfigDefinition configDefinition;
        private List<SingleDependencyDefinition> singleDependencyDefinitions = new ArrayList<>();
        private List<MultiDependencyDefinition> multiDependencyDefinitions = new ArrayList<>();
        private Constructor<?> constructor;
        private AutoLoad autoLoad = AutoLoad.disabled();

        public Builder withName(final String extensionName) {
            Objects.requireNonNull(extensionName, "name of extension must not be null");
            this.name = extensionName;
            return this;
        }

        public <T> Builder withExtension(final Class<T> spec, final Constructor<? extends T> construct) {
            Objects.requireNonNull(spec, "extension point class must not be null");
            Objects.requireNonNull(construct, "constructor must not be null");
            if (!Modifier.isPublic(construct.getModifiers())) {
                throw new IllegalArgumentException("constructor " + construct + " must be public");
            }
            this.constructor = construct;
            Class<? extends T> impl = construct.getDeclaringClass();
            if (!spec.isAssignableFrom(impl)) {
                throw new IllegalArgumentException(
                        String.format("implementation class (%s) doesn't implement specification class (%s)",
                                impl, spec));
            }
            this.extensionPointClass = spec;
            this.extensionClass = impl;
            return this;
        }

        public Builder withConfig(final ConfigDefinition config) {
            this.configDefinition = config;
            return this;
        }

        public Builder withSingleDependency(final SingleDependencyDefinition dependency) {
            Objects.requireNonNull(dependency, "dependency must not be null");
            this.singleDependencyDefinitions.add(dependency);
            return this;
        }

        public Builder withSingleDependencies(final List<SingleDependencyDefinition> dependencies) {
            Objects.requireNonNull(dependencies, "dependencies must not be null");
            this.singleDependencyDefinitions.addAll(dependencies);
            return this;
        }

        public Builder withMultiDependency(final MultiDependencyDefinition dependency) {
            Objects.requireNonNull(dependency, "dependency must not be null");
            this.multiDependencyDefinitions.add(dependency);
            return this;
        }

        public Builder withMultiDependencies(final List<MultiDependencyDefinition> dependencies) {
            Objects.requireNonNull(dependencies, "dependencies must not be null");
            this.multiDependencyDefinitions.addAll(dependencies);
            return this;
        }

        public Builder withAutoLoad(final AutoLoad autoLoad) {
            this.autoLoad = autoLoad;
            return this;
        }

        public Definition build() {
            if (name == null) {
                throw new IllegalStateException("name must be specified");
            }
            if (extensionPointClass == null || extensionClass == null || constructor == null) {
                throw new IllegalStateException("extension classes must be specified");
            }
            return new Definition(this);
        }
    }
}

