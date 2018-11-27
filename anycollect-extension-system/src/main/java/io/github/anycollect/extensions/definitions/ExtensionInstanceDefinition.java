package io.github.anycollect.extensions.definitions;

import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@ToString
public final class ExtensionInstanceDefinition {
    @Getter
    private final ExtensionDefinition extensionDefinition;
    @Getter
    private final String instanceName;
    private final Object config;
    private final Map<String, ExtensionInstanceDependencyDefinition> dependencies;

    public ExtensionInstanceDefinition(final ExtensionDefinition extensionDefinition,
                                       final String instanceName,
                                       final Object config,
                                       final Map<String, ExtensionInstanceDependencyDefinition> dependencies) {
        this.extensionDefinition = extensionDefinition;
        this.instanceName = instanceName;
        this.config = config;
        this.dependencies = dependencies;
    }

    public Optional<Object> getConfig() {
        return Optional.ofNullable(config);
    }

    public Map<String, ExtensionInstanceDependencyDefinition> getDependencies() {
        return Collections.unmodifiableMap(dependencies);
    }
}
