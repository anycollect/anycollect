package io.github.anycollect.extensions.dependencies;

import io.github.anycollect.extensions.Definition;
import io.github.anycollect.extensions.Instance;
import io.github.anycollect.extensions.di.InjectionPoint;
import io.github.anycollect.core.exceptions.ConfigurationException;
import io.github.anycollect.extensions.exceptions.WrongDependencyClassException;

public final class SingleDependencyDefinition extends AbstractDependencyDefinition {
    public SingleDependencyDefinition(final String name,
                                      final Class<?> parameterType,
                                      final boolean optional,
                                      final int position) {
        super(name, parameterType, optional, position, true);
    }

    public Dependency create(final Instance instance) {
        if (instance == null) {
            if (!isOptional()) {
                throw new ConfigurationException(getName() + " is required");
            } else {
                return new SingleDependency(null);
            }
        }
        Definition definition = instance.getDefinition();
        Class<?> extPointClass = definition.getExtensionPointClass();
        if (!getParameterType().equals(extPointClass)) {
            throw new WrongDependencyClassException(getName(), getParameterType(), extPointClass);
        }
        return new SingleDependency(instance);
    }

    private final class SingleDependency implements Dependency {
        private final Instance dependency;

        SingleDependency(final Instance dependency) {
            this.dependency = dependency;
        }

        @Override
        public InjectionPoint inject() {
            if (dependency == null) {
                return new InjectionPoint(null, SingleDependencyDefinition.this.getPosition());
            }
            return new InjectionPoint(dependency.resolve(), SingleDependencyDefinition.this.getPosition());
        }
    }
}
