package io.github.anycollect.extensions.definitions;

import io.github.anycollect.extensions.di.InjectionPoint;
import lombok.EqualsAndHashCode;

import java.util.Objects;

@EqualsAndHashCode
public final class ConfigDependency implements Dependency {
    private final ConfigDefinition definition;
    private final Object object;

    public ConfigDependency(final ConfigDefinition definition, final Object object) {
        Objects.requireNonNull(definition, "definition must not be null");
        if (object == null) {
            if (!definition.isOptional()) {
                throw new IllegalArgumentException("config must be passed");
            }
        } else {
            if (!definition.getParameterType().isAssignableFrom(object.getClass())) {
                throw new IllegalArgumentException("config must be assignable from "
                        + definition.getParameterType() + ", given: " + object.getClass());
            }
        }
        this.definition = definition;
        this.object = object;
    }

    @Override
    public InjectionPoint inject() {
        return new InjectionPoint(object, definition.getPosition());
    }
}
