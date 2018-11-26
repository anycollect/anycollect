package io.github.anycollect.extensions.definitions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

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
    private final Class<?> configClass;

    public static Builder builder() {
        return new Builder();
    }

    private ExtensionDefinition(final Builder builder) {
        this.name = builder.name;
        this.extensionPointClass = builder.extensionPointClass;
        this.extensionClass = builder.extensionClass;
        this.configClass = builder.configClass;
    }

    public Optional<Class<?>> getConfigClass() {
        return Optional.ofNullable(configClass);
    }

    public static final class Builder {
        private String name;
        private Class<?> extensionPointClass;
        private Class<?> extensionClass;
        private Class<?> configClass;

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

        public Builder withConfig(final Class<?> config) {
            this.configClass = config;
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

