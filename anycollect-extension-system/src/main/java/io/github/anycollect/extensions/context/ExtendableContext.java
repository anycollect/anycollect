package io.github.anycollect.extensions.context;

import io.github.anycollect.extensions.Definition;
import io.github.anycollect.extensions.Instance;

import javax.annotation.Nonnull;

public interface ExtendableContext extends Context {
    void addInstance(@Nonnull Instance instance);

    void addDefinition(@Nonnull Definition definition);
}
