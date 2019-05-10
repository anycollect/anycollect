package io.github.anycollect.extensions.loaders;

import io.github.anycollect.extensions.context.ExtendableContext;

import java.util.Arrays;
import java.util.List;

final class CompositeDefinitionLoader implements DefinitionLoader {
    private final List<DefinitionLoader> loaders;

    CompositeDefinitionLoader(final DefinitionLoader... loaders) {
        this.loaders = Arrays.asList(loaders);
    }

    @Override
    public void load(final ExtendableContext context) {
        for (DefinitionLoader loader : loaders) {
            loader.load(context);
        }
    }
}
