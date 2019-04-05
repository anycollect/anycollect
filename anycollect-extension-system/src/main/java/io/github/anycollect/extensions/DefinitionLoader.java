package io.github.anycollect.extensions;

import io.github.anycollect.extensions.definitions.Definition;
import io.github.anycollect.core.exceptions.ConfigurationException;

import java.util.Collection;

public interface DefinitionLoader {
    /**
     * Returns all extensions definitions.
     *
     * @throws   ConfigurationException   if configuration is wrong and cannot be loaded
     * @return                            list of {@link Definition}
     */
    Collection<Definition> load();

    static DefinitionLoader composite(DefinitionLoader... loaders) {
        return new CompositeDefinitionLoader(loaders);
    }
}
