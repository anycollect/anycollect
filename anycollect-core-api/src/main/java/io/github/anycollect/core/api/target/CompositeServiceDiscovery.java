package io.github.anycollect.core.api.target;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public final class CompositeServiceDiscovery<T extends Target> implements ServiceDiscovery<T> {
    private final List<? extends ServiceDiscovery<? extends T>> discoveries;

    public CompositeServiceDiscovery(final List<? extends ServiceDiscovery<? extends T>> discoveries) {
        this.discoveries = new ArrayList<>(discoveries);
    }

    @Override
    public Set<T> discover() {
        return discoveries.stream().flatMap(discovery -> discovery.discover().stream())
                .collect(toSet());
    }
}
