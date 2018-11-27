package io.github.anycollect.extensions.definitions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@ToString
@EqualsAndHashCode
public final class ExtensionDefinition {
    @Getter
    private final String name;
    @Getter
    private final Class<?> extensionPointClass;
    @Getter
    private final Class<?> extensionClass;
    private final ConfigParameterDefinition config;
    @Getter
    private final boolean nullableConfig;
    private final List<ExtensionDependencyDefinition> dependencies;

    public static Builder builder() {
        return new Builder();
    }

    private ExtensionDefinition(final Builder builder) {
        this.name = builder.name;
        this.extensionPointClass = builder.extensionPointClass;
        this.extensionClass = builder.extensionClass;
        this.config = builder.config;
        this.nullableConfig = builder.nullableConfig;
        this.dependencies = builder.dependencies;
    }

    public Optional<ConfigParameterDefinition> getConfig() {
        return Optional.ofNullable(config);
    }

    public static final class Builder {
        private String name;
        private Class<?> extensionPointClass;
        private Class<?> extensionClass;
        private ConfigParameterDefinition config;
        private boolean nullableConfig = true;
        private List<ExtensionDependencyDefinition> dependencies = new ArrayList<>();

        public Builder withName(final String extensionName) {
            Objects.requireNonNull(extensionName, "name of extension must not be null");
            this.name = extensionName;
            return this;
        }

        public <T> Builder withExtension(final Class<T> spec, final Class<? extends T> impl) {
            Objects.requireNonNull(spec, "extension point class must not be null");
            Objects.requireNonNull(impl, "extension class must not be null");
            if (!spec.isAssignableFrom(impl)) {
                throw new IllegalArgumentException(
                        String.format("implementation class (%s) doesn't implement specification class (%s)",
                                impl, spec));
            }
            this.extensionClass = spec;
            this.extensionPointClass = impl;
            return this;
        }

        public Builder withConfig(final ConfigParameterDefinition configDefinition) {
            this.config = configDefinition;
            return this;
        }

        public Builder withDependencies(final List<ExtensionDependencyDefinition> definitions) {
            Objects.requireNonNull(definitions, "dependencies must not be null");
            this.dependencies = new ArrayList<>(definitions);
            return this;
        }

        public ExtensionDefinition build() {
            if (name == null) {
                throw new IllegalStateException("name must be specified");
            }
            if (extensionPointClass == null || extensionClass == null) {
                throw new IllegalStateException("extension classes must be specified");
            }
            return new ExtensionDefinition(this);
        }
    }
}

