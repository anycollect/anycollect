package io.github.anycollect.extensions.dependencies;

import io.github.anycollect.extensions.Definition;
import io.github.anycollect.extensions.Instance;
import io.github.anycollect.extensions.di.InjectionPoint;
import io.github.anycollect.extensions.exceptions.WrongDependencyClassException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

public final class MultiDependencyDefinition extends AbstractDependencyDefinition {
    public MultiDependencyDefinition(final String name,
                                     final Class<?> parameterType,
                                     final boolean optional,
                                     final int position) {
        super(name, parameterType, optional, position, false);
    }

    public Dependency create(final List<Instance> instances) {
        Objects.requireNonNull(instances, "instances must not be null");
        for (Instance instance : instances) {
            Definition definition = instance.getDefinition();
            boolean found = false;
            for (final Class<?> contract : definition.getContracts()) {
                if (getParameterType().equals(contract)) {
                    found = true;
                }
            }
            if (!found) {
                throw new WrongDependencyClassException(getName(), getParameterType(), definition.getContracts());
            }
        }
        return new MultiDependency(instances);
    }

    private final class MultiDependency implements Dependency {
        private final List<Instance> dependencies;

        MultiDependency(final List<Instance> dependencies) {
            this.dependencies = new ArrayList<>(dependencies);
        }

        @Override
        public InjectionPoint inject() {
            return new InjectionPoint(
                    dependencies.stream().map(Instance::resolve).collect(toList()),
                    MultiDependencyDefinition.this.getPosition());
        }
    }
}
