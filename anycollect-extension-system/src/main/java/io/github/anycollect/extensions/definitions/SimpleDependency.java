package io.github.anycollect.extensions.definitions;

import io.github.anycollect.extensions.di.InjectionPoint;

public final class SimpleDependency implements Dependency {
    private final InjectionPoint injectionPoint;

    public SimpleDependency(final Object dependency, final int position) {
        this.injectionPoint = new InjectionPoint(dependency, position);
    }

    @Override
    public InjectionPoint inject() {
        return injectionPoint;
    }
}
