package io.github.anycollect.extensions;

import io.github.anycollect.extensions.definitions.Definition;
import io.github.anycollect.extensions.exceptions.ConfigurationException;

import java.util.Collection;

public interface ExtensionDefinitionLoader {
    /**
     * Returns all extensions definitions.
     *
     * @throws   ConfigurationException   if configuration is wrong and cannot be loaded
     * @return                            list of {@link Definition}
     */
    Collection<Definition> load();
}
