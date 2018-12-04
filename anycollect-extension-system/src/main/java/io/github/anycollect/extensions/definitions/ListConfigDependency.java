package io.github.anycollect.extensions.definitions;

import io.github.anycollect.extensions.di.InjectionPoint;

import java.util.List;
import java.util.Objects;

public final class ListConfigDependency implements Dependency {
    private final ConfigDefinition definition;
    private final List<?> objects;

    ListConfigDependency(final ConfigDefinition definition, final List<?> objects) {
        this.definition = definition;
        this.objects = objects;
        Objects.requireNonNull(definition, "definition must not be null");
        Objects.requireNonNull(objects, "objects must not be null");
        for (Object object : objects) {
            if (!definition.getParameterType().equals(object.getClass())) {
                throw new IllegalArgumentException("config must be of type "
                        + definition.getParameterType() + " instead of " + object.getClass());
            }
        }
    }

    @Override
    public InjectionPoint inject() {
        return new InjectionPoint(objects, definition.getPosition());
    }
}
