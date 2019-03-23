package io.github.anycollect.extensions;

import io.github.anycollect.core.exceptions.ConfigurationException;
import io.github.anycollect.extensions.definitions.ExtendableContext;

import javax.annotation.Nonnull;

public interface InstanceLoader {
    /**
     * Returns all extensions instances definitions.
     *
     * @throws ConfigurationException   if configuration is wrong and cannot be loaded
     */
    void load(@Nonnull ExtendableContext context);
}
