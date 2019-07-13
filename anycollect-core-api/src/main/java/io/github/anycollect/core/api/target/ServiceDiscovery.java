package io.github.anycollect.core.api.target;

import java.util.List;
import java.util.Set;

public interface ServiceDiscovery<T extends Target> {
    static <T extends Target> ServiceDiscovery<T> composite(List<? extends ServiceDiscovery<? extends T>> discoveries) {
        return new CompositeServiceDiscovery<>(discoveries);
    }

    static <T extends Target> ServiceDiscovery<T> singleton(T target) {
        return new SingletonServiceDiscovery<>(target);
    }

    Set<? extends T> discover();
}
