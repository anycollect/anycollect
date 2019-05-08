package io.github.anycollect.extensions.loaders;

import io.github.anycollect.core.exceptions.ConfigurationException;
import io.github.anycollect.extensions.context.ExtendableContext;
import io.github.anycollect.extensions.scope.Scope;
import io.github.anycollect.extensions.substitution.VarSubstitutor;

import javax.annotation.Nonnull;

public interface InstanceLoader {
    Scope getScope();

    VarSubstitutor getVarSubstitutor();

    /**
     * Returns all extensions instances definitions.
     *
     * @throws ConfigurationException if configuration is wrong and cannot be loaded
     */
    void load(@Nonnull ExtendableContext context);
}
