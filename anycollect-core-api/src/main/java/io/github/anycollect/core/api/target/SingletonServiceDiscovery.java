package io.github.anycollect.core.api.target;

import java.util.Collections;
import java.util.Set;

public final class SingletonServiceDiscovery<T extends Target> implements ServiceDiscovery<T> {
    private final Set<T> target;

    public SingletonServiceDiscovery(final T target) {
        this.target = Collections.singleton(target);
    }

    @Override
    public Set<T> discover() {
        return target;
    }
}
