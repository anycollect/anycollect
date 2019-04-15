package io.github.anycollect.extensions.loaders;

import io.github.anycollect.extensions.Definition;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

final class CompositeDefinitionLoader implements DefinitionLoader {
    private final List<DefinitionLoader> loaders;

    CompositeDefinitionLoader(final DefinitionLoader... loaders) {
        this.loaders = Arrays.asList(loaders);
    }

    @Override
    public Collection<Definition> load() {
        return loaders.stream()
                .map(DefinitionLoader::load)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
