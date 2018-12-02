package io.github.anycollect.extensions.di;

import lombok.Getter;

@Getter
public final class InjectionPoint {
    private final Object dependency;
    private final int position;

    public InjectionPoint(final Object dependency, final int position) {
        this.dependency = dependency;
        this.position = position;
    }
}
