package io.github.anycollect.extensions.loaders;

import io.github.anycollect.core.exceptions.ConfigurationException;
import io.github.anycollect.extensions.Definition;
import io.github.anycollect.extensions.context.ContextImpl;
import io.github.anycollect.extensions.context.ExtendableContext;

import java.util.Collection;
import java.util.Collections;

public interface DefinitionLoader {
    /**
     * Returns all extensions definitions.
     *
     * @throws   ConfigurationException   if configuration is wrong and cannot be loaded
     * @return                            list of {@link Definition}
     */
    @Deprecated
    default Collection<Definition> load() {
        ContextImpl context = new ContextImpl(Collections.emptyList());
        load(context);
        return context.getDefinitions();
    }

    void load(ExtendableContext context);

    static DefinitionLoader composite(DefinitionLoader... loaders) {
        return new CompositeDefinitionLoader(loaders);
    }
}
