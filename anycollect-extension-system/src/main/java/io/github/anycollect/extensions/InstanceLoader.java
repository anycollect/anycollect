package io.github.anycollect.extensions;

import io.github.anycollect.extensions.definitions.Instance;
import io.github.anycollect.core.exceptions.ConfigurationException;

import java.util.Collection;

public interface InstanceLoader {
    /**
     * Returns all extensions instances definitions.
     *
     * @throws ConfigurationException   if configuration is wrong and cannot be loaded
     * @return                          list of {@link Instance}
     */
    Collection<Instance> load();
}
